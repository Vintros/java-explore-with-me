package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.RequestHit;
import ru.practicum.model.ResponseHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface StatsRepository extends JpaRepository<RequestHit, Long> {

    @Query(value = "select new ResponseHit(r.app, r.uri, count(r.id)) from RequestHit r " +
            "where r.uri in :uris " +
            "and r.timestamp between :start and :end " +
            "group by r.app, r.uri " +
            "order by count(r.id) DESC")
    List<ResponseHit> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new ResponseHit(r.app, r.uri, count(r.id)) from RequestHit r " +
            "where r.ip in (select distinct(i.ip) from RequestHit i group by i.ip) " +
            "and r.uri in :uris " +
            "and r.timestamp between :start and :end " +
            "group by r.app, r.uri " +
            "order by count(r.id) DESC")
    List<ResponseHit> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

}
