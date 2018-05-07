package com.zx.seckill.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import com.zx.seckill.pojo.OrderInfo;
import com.zx.seckill.pojo.SeckillOrder;

@Mapper
public interface OrderDao {
	//可以在seckill_order建一个userid和goodsId的组合的唯一索引，保证每个用户只能秒杀一个商品
	//就不建了因为本地用户太少

	@Select("select * from seckill_order where user_id = #{userId} and goods_id = #{goodsId}")
	public SeckillOrder getSeckillOrederByUserIdGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);

	@Insert("insert into order_info(user_id,goods_id,delivery_addr_id,goods_name,goods_count,goods_price,status,create_date,pay_date)"
			+ "values(#{userId},#{goodsId},null,#{goodsName},#{goodsCount},#{goodsPrice}"
			+ ",#{status},#{createDate},null)")
	@SelectKey(keyColumn="id",keyProperty="id",resultType=long.class,before=false,statement="select last_insert_id()")
	public long insertOrder(OrderInfo orderInfo);

	@Insert("insert into seckill_order(user_id,order_id,goods_id) values("
			+ "#{userId},#{orderId},#{goodsId})")
	public int insertSeckillDao(SeckillOrder seckillOrder);

	@Select("select * from order_info where id = #{orderId}")
	public OrderInfo getOrderById(@Param("orderId")long orderId);

}
