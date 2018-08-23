package com.example.demo.dao;

import com.example.demo.bean.GoodsCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;


public interface GoodsCartDao extends JpaRepository<GoodsCart,Long> {
//       List<GoodsCart> findByGoods_id(Integer goods_id);
       GoodsCart findByCount(Integer count);
       @Query("select p from GoodsCart p where p.goods_id= ?1 ")
       GoodsCart findByGoods_id(Long goods_id);
       @Query("select  p from GoodsCart p where p.sc_id=?1 and deleteStatus = 0" )
       List<GoodsCart> findBySc_id(Long sc_id);
       @Query("select p from GoodsCart p where p.goods_id=?1 and p.sc_id=?2 and p.spec_info=?3")
       GoodsCart findByGoods_idAndSc_idAndSpec_info(Long goods_id,Long sc_id,String spec_idfo);
       @Query("select p from GoodsCart p where p.goods_id=?1 and p.sc_id=?2")
       GoodsCart findByGoods_idAndSc_id(Long goods_id,Long sc_id);

       @Query(value = "select p.goods_id from GoodsCart p where p.of_id=?1")
       List<Long> findGoodsIdByOrderForm(Long of_id);

        @Modifying(clearAutomatically = true)
        @Transactional
        @Query("update GoodsCart p set p.deleteStatus = 1 , p.count = 0 where p.sc_id in ?1 and p.goods_id = ?2 and p.deleteStatus = 0")
        void deleteByScIdAndGoodsId(List<Long> scIds, Long goodsId);

}
