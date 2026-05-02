package com.example.lost_found_backend.mapper;

import com.example.lost_found_backend.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物品 Mapper
 */
@Mapper
public interface ItemMapper {

    int insert(Item item);

    Item findById(@Param("id") Long id);

    int updateById(Item item);

    int deleteById(@Param("id") Long id);

    /**
     * 分页查询物品列表（支持条件筛选）
     *
     * @param type     物品类型，null=全部
     * @param status   物品状态，null=全部
     * @param category 分类，null=全部
     * @param keyword  关键词搜索（标题+描述），null=不过滤
     * @param sort     排序字段：create_time
     * @param order    排序方式：asc/desc
     * @param offset   偏移量
     * @param size     每页条数
     */
    List<Item> list(@Param("type") Integer type,
                    @Param("status") Integer status,
                    @Param("category") String category,
                    @Param("keyword") String keyword,
                    @Param("sort") String sort,
                    @Param("order") String order,
                    @Param("offset") int offset,
                    @Param("size") int size);

    /** 统计符合条件的总数 */
    long count(@Param("type") Integer type,
               @Param("status") Integer status,
               @Param("category") String category,
               @Param("keyword") String keyword);

    /** 根据发布者openid查询自己发布的物品 */
    List<Item> findByPublisher(@Param("publisherOpenid") String publisherOpenid,
                               @Param("offset") int offset,
                               @Param("size") int size);

    long countByPublisher(@Param("publisherOpenid") String publisherOpenid);

    int incrementViewCount(@Param("id") Long id);

    int incrementCommentCount(@Param("id") Long id);

    int decrementCommentCount(@Param("id") Long id);

    int incrementCollectionCount(@Param("id") Long id);

    int decrementCollectionCount(@Param("id") Long id);
}
