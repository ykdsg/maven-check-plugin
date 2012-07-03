package com.taobao.ju.maven.tair;

/**
 * User: duxing
 * Date: 2011-11-22 10:40:43
 */
public interface TairAreaConstant {

    public static final int BASE    =   0;

    //商品对应的任务列表
    public static final int JU_ITEM_TASK = 1 + BASE;

    //单个商品的缓存
    public static final int JU_ITEM = 3 + BASE;

    //今天聚划算的商品列表
    public static final int JU_ITEM_TODAY = 4 + BASE;

    //总的任务列表
    public static final int TASK = 6 + BASE;

    //今日聚划算的组缓存列表
    public static final int GROUP_LIST = 8 + BASE;

    //单个商品缓存
    public static final int ITEM_DATA = 9 + BASE;

    //单个商品缓存
    public static final int ITEM = 10 + BASE;

    //今日商品的id列表
    public static final int ITEM_ID_LIST = 11 + BASE;

    //资源独立分区
    public static final int RESOURCE    =   13 + BASE;

    //added by xiaojiantao 聚划算订阅信息
    public static final int SUBSCRIBE = 15 + BASE;

    // 聚划算订阅方式（邮件、旺旺等）
    public static final int SUBSCRIBE_INFO = 16 + BASE;

    //聚划算计数中心
    public static final int COUNT = 17 + BASE;

    //聚划算阶梯价格
    public static final int STAIR = 18 + BASE;

    //聚划算首页显示的出价记录
    public static final int ORDER_LIST = 19 + BASE;

    //聚划算类目商品，存放的是类目所属的商品信息
    public static final int CATEGORY = 21 + BASE;

    //存放聚划算的类目配置信息，包括淘宝的一级类目的数据
    public static final int CATEGORY_CONFIGURE = 22 + BASE;


    //一城多团 id对应城市的缓存
    public static final int ITEMID_CITY = 28+BASE;

    //问题式团购 商品问题
    public static final int ITEM_QA = 29+BASE;

    //聚划算类目的缓存
    public static final int JU_CATEGORY = 30 + BASE;
    //聚划算类目关联的缓存
    public static final int JU_CATEGORY_RELATION = 31 + BASE;

    //投票眼光缓存
    public static final int ONLINE_ITEM_VOTE_VISION = 32 + BASE;

    //seller缓存
    public static final int SELLER = 40 + BASE;
    /**商品关联关系**/
    public static final int ITEM_ASSOCIATION = 44 + BASE;
}
