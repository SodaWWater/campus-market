package com.liminghan.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liminghan.market.entity.MarketGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MarketGoodsMapper extends BaseMapper<MarketGoods> {

    @Update("""
            UPDATE market_goods
            SET status = #{toStatus}, updated_at = NOW()
            WHERE id = #{goodsId} AND status = #{fromStatus}
            """)
    int updateStatusIfMatch(@Param("goodsId") Long goodsId,
                            @Param("fromStatus") String fromStatus,
                            @Param("toStatus") String toStatus);
}
