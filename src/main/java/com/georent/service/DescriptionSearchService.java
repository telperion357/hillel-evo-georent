package com.georent.service;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.dto.LotPageDTO;
import com.georent.dto.LotPageable;
import com.georent.exception.SearchConnectionNotAvailableException;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("unchecked")
public class DescriptionSearchService {

    private final EntityManagerFactory entityManagerFactory;
    private final LotService lotService;
    private final AWSS3Service awss3Service;

    @Autowired
    public DescriptionSearchService(EntityManagerFactory entityManagerFactory,
                                    LotService lotService,
                                    AWSS3Service awss3Service) {
        this.entityManagerFactory = entityManagerFactory;
        this.lotService = lotService;
        this.awss3Service = awss3Service;
    }

    /**
     * Search allLots with filters: "address" to the field address in class Coordinates
     * Search allLots with filters: "lotname" to the field lotName in class Description
     * and/or: if param equals !isBlank, not filters to this param
     *
     * @param address
     * @param lotName
     * @return all lots with filters: "address" and "lotname" in the format  Set<LotPageDTO>
     */
    public Set<LotPageDTO> fuzzyLotNameAndAddressSearch(String address, String lotName) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        try (Session currentSession = sessionFactory.openSession();
             FullTextSession fullTextSession = Search.getFullTextSession(currentSession)) {

            Map<Long, LotPageDTO> hm = new HashMap<>();
            if (!StringUtils.isBlank(address)) {
                List<Coordinates> coordinates = getLotsSearchAdr(fullTextSession, address);
                hm = coordinates
                        .stream()
                        .map(this::mapCoordinatesDTO)
                        .collect(Collectors.toMap(LotPageDTO::getId, Function.identity()));
            }
            if (!StringUtils.isBlank(lotName)) {
                List<Description> descriptions = getLotsSearchLotName(fullTextSession, lotName);
                Map<Long, LotPageDTO> hmSrc = descriptions
                        .stream()
                        .map(this::mapDescriptionDTO)
                        .collect(Collectors.toMap(LotPageDTO::getId, Function.identity()));
                hm.putAll(hmSrc);
            }

            Set<LotPageDTO> valueSet = new HashSet<>(hm.values());

            return valueSet
                    .stream()
                    .sorted(Comparator.comparing(LotPageDTO::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (CannotCreateTransactionException e) {

            throw new CannotCreateTransactionException(e.getMessage(), e.getCause());

        } catch (PersistenceException e) {

            throw new SearchConnectionNotAvailableException(e.getMessage(), e.getCause());

        }
    }


    /**
     * Search allLots with filters:@Slf4j "address" to the field address in class Coordinates
     * Search allLots with filters: "lotname" to the field lotName in class Description
     * and/or: if param equals !isBlank, not filters to this param
     *
     * @param pageNumber
     * @param count
     * @param methodPage
     * @param address
     * @param lotName    List<Long> ids - result all lotId after search with filters: "address" and "lotname"
     * @param andOr      filtre search:  value == true - "&&" ; value = false "||"
     * @return list of all lots (filter) one page in the format of List<LotPageDTO> with pageNumber, totalPages..
     */
    public LotPageable fuzzyLotPageNameAndAddressSearch(int pageNumber, int count, String methodPage, String address, String lotName, boolean andOr) {
        if (StringUtils.isBlank(address) && StringUtils.isBlank(lotName)) {
            return this.lotService.getPage(pageNumber, count, methodPage, null);
        } else {
            SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
            try (Session currentSession = sessionFactory.openSession();
                 FullTextSession fullTextSession = Search.getFullTextSession(currentSession)) {
                Set<Long> idSet = new HashSet<>();
                Set<Long> src = new HashSet<>();
                List<Long> ids = new ArrayList<>();
                if (!StringUtils.isBlank(address)) {
                    List<Coordinates> coordinates = getLotsSearchAdr(fullTextSession, address);
                    idSet = coordinates
                            .stream()
                            .map(Coordinates::getId)
                            .collect(Collectors.toSet());
                }
                if (!StringUtils.isBlank(lotName)) {
                    List<Description> descriptions = getLotsSearchLotName(fullTextSession, lotName);
                    src = descriptions
                            .stream()
                            .map(Description::getId)
                            .collect(Collectors.toSet());
                }
                if (andOr && !StringUtils.isBlank(lotName) && !StringUtils.isBlank(address)) {    //? && == true !StringUtils.isBlank(lotName) - ok !StringUtils.isBlank(address) - ok
                    if (idSet.size() > 0 && src.size() > 0) {
                        Iterator iteratorIdSet = idSet.iterator();
                        while (iteratorIdSet.hasNext()) {
                            Long id = (Long) iteratorIdSet.next();
                            System.out.println(id);
                            Iterator iteratorsrc = src.iterator();
                            while (iteratorsrc.hasNext()) {
                                Long idSrc = (Long) iteratorsrc.next();
                                System.out.println(idSrc);
                                if (id == idSrc) {
                                    ids.add(id);
                                    break;
                                }
                            }
                        }
                    }
                } else {          //? || == false;  StringUtils.isBlank(lotName) - ok; StringUtils.isBlank(address) - ok
                    idSet.addAll(src);
                    ids = new ArrayList<>(idSet);
                }
                return this.lotService.getPage(pageNumber, count, methodPage, ids);
            } catch (CannotCreateTransactionException e) {
                throw new CannotCreateTransactionException(e.getMessage(), e.getCause());
            } catch (PersistenceException e) {
                throw new SearchConnectionNotAvailableException(e.getMessage(), e.getCause());
            }

        }
    }

    /**
     * @param fullTextSession
     * @param address
     * @return List<Coordinates> with filters: "address"
     */
    private List<Coordinates> getLotsSearchAdr(FullTextSession fullTextSession, String address) {

        QueryBuilder queryBuilder = fullTextSession
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Coordinates.class)
                .get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(10)
                .onFields("address")
                .matching(address)
                .createQuery();

        javax.persistence.Query fullTextQuery = fullTextSession.createFullTextQuery(query, Coordinates.class);

        return (List<Coordinates>) fullTextQuery.getResultList();
    }

    /**
     * @param fullTextSession
     * @param lotName
     * @return List<Description> with filters: "lotName"
     */
    private List<Description> getLotsSearchLotName(FullTextSession fullTextSession, String lotName) {

        QueryBuilder queryBuilder = fullTextSession
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Description.class).get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(10)
                .onFields("lotName")
                .matching(lotName)
                .createQuery();
        javax.persistence.Query fullTextQuery = fullTextSession.createFullTextQuery(query, Description.class);

        return (List<Description>) fullTextQuery.getResultList();

    }

    private LotPageDTO mapCoordinatesDTO(Coordinates coordinates) {
        LotPageDTO dto = new LotPageDTO();
        dto.setId(coordinates.getLot().getId());
        dto.setPrice(coordinates.getLot().getPrice());
        dto.setAddress(coordinates.getAddress());
        dto.setLotName(coordinates.getLot().getDescription().getLotName());
        dto.setImageUrl(getUrl(dto.getId()));
        return dto;
    }

    private LotPageDTO mapDescriptionDTO(Description description) {
        LotPageDTO dto = new LotPageDTO();
        dto.setId(description.getLot().getId());
        dto.setPrice(description.getLot().getPrice());
        dto.setAddress(description.getLot().getCoordinates().getAddress());
        dto.setLotName(description.getLotName());
        dto.setImageUrl(getUrl(dto.getId()));
        return dto;
    }

    private URL getUrl(Long lotId) {
        List<DeleteObjectsRequest.KeyVersion> keys = this.awss3Service.getKeysLot(lotId);
        return (keys.size() > 0) ? this.awss3Service.generatePresignedURL(keys.get(0).getKey()) : null;
    }

}
