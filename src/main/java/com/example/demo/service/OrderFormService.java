package com.example.demo.service;

import ch.qos.logback.core.joran.conditional.ElseAction;
import com.example.demo.bean.*;
import com.example.demo.dao.*;
import com.example.demo.util.OrderFormStatusString;
import com.example.demo.util.SqlDate;
import com.example.demo.valuable.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Create by coldwarm on 2018/8/10.
 */
@Service
public class OrderFormService {

    @Autowired
    private OrderFormDao orderFormDao;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private AccessoryDao accessoryDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserDao userDao;


    public List<OrderFormAllValuable> findAll(Long store_id){
       List<OrderFormAllValuable> orderFormAllValuables = new ArrayList<>();
       List<OrderForm> orderForms = orderFormDao.findByStore_id(store_id);

       for (OrderForm orderForm :orderForms){
           List<Long> goodsId = goodsService.findGoodsIdByOf_id(orderForm.getId());
           List<GoodsMyValuable> goodsMyValuables=new ArrayList<>();
           Double totalPrice = 0.0;
           for (Long l : goodsId){
               Goods goods = goodsDao.findById(l).orElse(new Goods());
               Accessory accessory = accessoryDao.findById(goods.getGoods_main_photo_id()).orElse(new Accessory());
               goodsMyValuables.add(new GoodsMyValuable(goods.getGoods_name(),new ZuTu(accessory.getPath(),accessory.getName(),accessory.getExt()),goods.getGoods_price()));
               totalPrice =totalPrice + goods.getGoods_price();
           }
           if (addressDao.findByIdd(orderForm.getAddr_id()).isDeleteStatus() == false) {
               String address = addressService.area(orderForm.getAddr_id()) + addressDao.findByIdAndDeleteStatusEquals(orderForm.getAddr_id(), false).getArea_info();
               orderFormAllValuables.add(new OrderFormAllValuable(goodsMyValuables, orderForm.getGoods_amount(),
                       OrderFormStatusString.stringStatus(orderForm.getOrder_status()), address, orderForm.getOrder_id(), orderForm.getId(), totalPrice));
           }else {
               orderFormAllValuables.add(new OrderFormAllValuable(goodsMyValuables, orderForm.getGoods_amount(),
                       OrderFormStatusString.stringStatus(orderForm.getOrder_status()), orderForm.getOrder_id(), orderForm.getId(),totalPrice));
           }

       }

       return orderFormAllValuables;
    }

    public List<OrderFormValuable> findByStore_idAndOrder_status(Long store_id,int order_status){

        List<OrderFormValuable> orderFormValuables = new ArrayList<>();
        List<OrderForm> orderForm = orderFormDao.findByStore_idAndOrder_status(store_id, order_status);
        for (OrderForm orderForm1 : orderForm){
            List<Long> goodsId = goodsService.findGoodsIdByOf_id(orderForm1.getId());
            List<GoodsMyValuable> goodsMyValuables=new ArrayList<>();
            Double totalPrice = 0.0;
            for (Long id : goodsId){
                Goods goods=goodsDao.findById(id).orElse(new Goods());
                Accessory accessory=accessoryDao.findById(goods.getGoods_main_photo_id()).orElse(new Accessory());
                goodsMyValuables.add(new GoodsMyValuable(goods.getGoods_name(),new ZuTu(accessory.getPath(),accessory.getName(),accessory.getExt()),goods.getGoods_price()));
                totalPrice =totalPrice + goods.getGoods_price();
            }
            orderFormValuables.add(new OrderFormValuable(goodsMyValuables,orderForm1.getGoods_amount(),
                    OrderFormStatusString.stringStatus(orderForm1.getOrder_status()),orderForm1.getId(),totalPrice));
        }
        return orderFormValuables;
    }

    public List<OrderFormByIdValuable> findByOf_id(Long of_id){
        List<OrderFormByIdValuable> orderFormByIdValuables = new ArrayList<>();
        List<OrderForm> orderForms = orderFormDao.findByOf_id(of_id);
        for (OrderForm orderForm : orderForms) {
            List<Long> goodsId = goodsService.findGoodsIdByOf_id(orderForm.getId());
            List<GoodsMyValuable> goodsMyValuables = new ArrayList<>();
            Double totalPrice =0.0 ;
            for (Long l : goodsId) {
                Goods goods = goodsDao.findById(l).orElse(new Goods());
                Accessory accessory = accessoryDao.findById(goods.getGoods_main_photo_id()).orElse(new Accessory());
                goodsMyValuables.add(new GoodsMyValuable(goods.getGoods_name(), new ZuTu(accessory.getPath(), accessory.getName(), accessory.getExt()), goods.getGoods_price()));
                totalPrice =totalPrice + goods.getGoods_price();
            }
            Address address1 = addressDao.findById(orderForm.getAddr_id()).orElse(new Address());
            Long addr_id = orderFormDao.findAddr_idByOf_id(of_id);
            if (addressDao.findByIdd(orderFormDao.findAddr_idByOf_id(of_id)).isDeleteStatus() == false) {
                String address = addressService.area(addr_id) + addressDao.findByIdAndDeleteStatusEquals(addr_id, false).getArea_info();
                orderFormByIdValuables.add(new OrderFormByIdValuable(goodsMyValuables, orderForm.getGoods_amount(),
                        OrderFormStatusString.stringStatus(orderForm.getOrder_status()),address1.getTrueName(), address1.getTelephone(),address1.getMobile(),address, orderForm.getOrder_id(),
                        totalPrice,SqlDate.dataToString(orderForm.getAddtime()),SqlDate.dataToString(orderForm.getFinishtime()),
                        SqlDate.dataToString(orderForm.getPaytime()),SqlDate.dataToString(orderForm.getShiptime()),orderForm.getShipcode()));
            }else {
                orderFormByIdValuables.add(new OrderFormByIdValuable(goodsMyValuables, orderForm.getGoods_amount(),
                        OrderFormStatusString.stringStatus(orderForm.getOrder_status()),address1.getTrueName(), address1.getTelephone(),address1.getMobile(),orderForm.getOrder_id(),
                        totalPrice,SqlDate.dataToString(orderForm.getAddtime()),SqlDate.dataToString(orderForm.getFinishtime()),
                        SqlDate.dataToString(orderForm.getPaytime()),SqlDate.dataToString(orderForm.getShiptime()),orderForm.getShipcode()));
            }
        }
        return orderFormByIdValuables;
    }

   public List<OrderForm> findBetweenOrderForm(Long storeId,String  date1,String  date2) throws ParseException {
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
       Date d1 = sdf.parse(date1);
       Date d2 = sdf.parse(date2);
       return orderFormDao.findByFinishtimeBetween(storeId,d1,d2);

    }


}
