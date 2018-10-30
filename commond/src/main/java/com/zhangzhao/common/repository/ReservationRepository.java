package com.zhangzhao.common.repository;

import com.zhangzhao.common.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 预约订单
 *
 * @author Administrator
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {


    @Modifying(clearAutomatically = true)
    @Query(value = "update reservation set province=:province,district=:district,city=:city,detailedAddress=:detailedAddress where id=:id", nativeQuery = true)
    void updateAddress(@Param("id") Long id, @Param("province") String province, @Param("district") String district, @Param("city") String city, @Param("detailedAddress") String detailedAddress);

    @Modifying(clearAutomatically = true)
    @Query(value = " update reservation set status=:status where  id=:id", nativeQuery = true)
    void updateStatus(@Param("id") Long id, @Param("status") int status);

    @Modifying(clearAutomatically = true)
    @Query(value = " update reservation set statusOk=:statusOk where  id=:id", nativeQuery = true)
    void updatestatusOk(@Param("id") Long id, @Param("statusOk") int statusOk);

    Reservation findByreservationNumber(String reservationNumber);

    Optional<Reservation> findById(@Param("id") Long id);

    List<Reservation> findByIdAndStatus(Long id, Integer[] status);

    /**
     * 累计订单统计
     *
     * @param status
     * @return
     */
    @Query(value = " select count(*) from reservation where status=5", nativeQuery = true)
    int selectByStatus(int status);

    /**
     * 日订单统计
     *
     * @param status
     * @return
     */
    @Query(value = "select count(*) from reservation where status=5 and DAY(finish_time)=DAY(NOW())", nativeQuery = true)
    int selectAllocateTimeDays(int status);

    /**
     * 累计收支
     *
     * @param incomePrice
     * @return
     */
    @Query(value = "select SUM(income_price) from reservation WHERE status=5", nativeQuery = true)
    double selectAddShouzhi(double incomePrice);

    /**
     * 昨日收支
     *
     * @param incomePrice
     * @return
     */
    @Query(value = "SELECT SUM(income_price) FROM reservation WHERE allocate_time >= CURDATE() AND allocate_time < DATE_SUB(CURDATE(),INTERVAL -1 DAY)", nativeQuery = true)
    double selectyesterdayShouzhi(double incomePrice);


    /**
     * 好评数
     *
     * @return
     */
    @Query(value = "select count(*) from reservation where status=5 and star>=3", nativeQuery = true)
    int countStars(int status);

    /**
     * 准时数
     *
     * @param status
     * @return
     */
    @Query(value = "SELECT count(*) from reservation where status=5 and finish_time<=appointment_time", nativeQuery = true)
    int zhunShiShu(int status);

    /**
     * 当前排名
     *
     * @param masterId
     * @return
     */
    @Query(value = "select (select count(*) from reservation where r.hao_pinglv<=hao_pinglv or (r.hao_pinglv=hao_pinglv and r.finish_time<finish_time)) as 排名 from reservation r where master_id=:masterId", nativeQuery = true)
    int preantranking(@Param("masterId") Long masterId);

    /**
     * 月排行
     *
     * @return
     */
    @Query(value = "SELECT r.hao_pinglv,u.icon,u.name from reservation r LEFT JOIN user u on (u.id=r.master_id) WHERE DATE_FORMAT(finish_time,'%Y-%m') = DATE_FORMAT(NOW(),'%Y-%m') order by hao_pinglv desc", nativeQuery = true)
    List<String> monthRanking();

    /**
     * 季度排行
     *
     * @return
     */
    @Query(value = "SELECT r.hao_pinglv,u.icon,u.name FROM reservation r LEFT JOIN user u on (u.user_id=r.master_id) WHERE quarter( FROM_UNIXTIME( finish_time )) = quarter( CURDATE( )) order by hao_pinglv desc", nativeQuery = true)
    List<String> quarterRanking();

    /**
     * 年排名
     *
     * @return
     */
    @Query(value = "SELECT r.hao_pinglv,u.icon,u.name from reservation r LEFT JOIN user u on (u.user_id=r.master_id) WHERE DATE_FORMAT(finish_time,'%Y') = DATE_FORMAT(NOW(),'%Y') order by hao_pinglv desc", nativeQuery = true)
    List<String> yearRanking();

}
