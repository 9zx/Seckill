package com.zx.seckill.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zx.seckill.pojo.SeckillGoods;
import com.zx.seckill.vo.GoodsVO;

//Mapper注解使springBoot-mybaits集成的一个注解，加了它的就标志为一个dao了，结合@Select，insert，deleta，result(结果集映射)
//直接完成dao+mapper工作，更简便
//或者在主类上写@MapperScan("com.zx.seckill.dao")扫描dao包，这样dao下的每个类就不用写@Mapper注解了，更简便了一点
//Spring+Mybatis时还要注解dao+扫描mapper文件，springBoot这里直接dao+mapper在一起解决了，但解耦好像差了点
@Mapper
public interface GoodsDao {

	@Select("select g.*,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date "
			+ "from seckill_goods sg left join goods g on sg.goods_id=g.id ")
	public List<GoodsVO> getGoodsVOList();

	@Select("select g.*,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date "
			+ "from seckill_goods sg left join goods g on sg.goods_id=g.id "
			+ "where sg.id = #{goodsId}")
	public GoodsVO getGoodsVOByGoodsId(@Param("goodsId")long goodsId);

	
	@Update("update seckill_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
	public int reduceStockCount(SeckillGoods goods);
}
