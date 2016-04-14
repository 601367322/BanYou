package com.quanliren.quan_one.util;

public class URL {


    public static final String URL = "http://" + Util.getPropertiesValue("ip") + ":" + Util.getPropertiesValue("port");
    public static final String IP = Util.getPropertiesValue("ip");
    public static final Integer PORT = Integer.valueOf(Util.getPropertiesValue("socket"));


    public static final String STATUS = "status";
    public static final String RESPONSE = "responses";
    public static final String INFO = "info";
    public static final String PAGEINDEX = "p";
    public static final String TOTAL = "total";
    public static final String COUNT = "count";
    public static final String LIST = "list";

    /**
     * 注册第一步填写手机号
     **/
    public static final String REG_FIRST = URL + "/client/send_user_mobile.php";
    /**
     * 注册第二步填写验证码
     **/
    public static final String REG_SENDCODE = URL + "/client/send_user_auth.php";
    /**
     * 注册第三步填写基本信息
     **/
    public static final String REG_THIRD = URL + "/client/send_reg_info.php";
    /**
     * 找回密码第一步
     **/
    public static final String FINDPASSWORD_FIRST = URL + "/client/forget_pwd_one.php";
    /**
     * 找回密码第二部
     **/
    public static final String FINDPASSWORD_SECOND = URL + "/client/forget_pwd_two.php";
    /**
     * 修改密码
     **/
    public static final String MODIFYPASSWORD = URL + "/client/user/alert_pwd.php";
    /**
     * 退出
     **/
    public static final String LOGOUT = URL + "/client/logout.php";
    /**
     * 登陆
     **/
    public static final String LOGIN = URL + "/client/user_login.php";
    /**
     * 编辑用户信息
     **/
    public static final String EDIT_USER_INFO = URL + "/client/user/edit_info.php";
    /**
     * 上传用户头像
     **/
    public static final String UPLOAD_USER_LOGO = URL + "/client/user/img/avatar/upload.php";
    /**
     * 获取用户详细信息
     **/
    public static final String GET_USER_INFO = URL + "/client/user/get_user_detail.php";
    /**
     * 删除动态
     **/
    public static final String DELETE_DONGTAI = URL + "/client/user/dynamic/cancel_dynamic.php";
    /**
     * 删除头像
     **/
    public static final String SET_PICTURE = URL + "/client/user/img/avatar/update_num.php";
    /**
     * 发表约会
     **/
    public static final String PUBLISH_TXT = URL + "/client/user/dynamic/pub_text.php";
    /**
     * 发表约会图片
     **/
    public static final String PUBLISH_IMG = URL + "/client/user/dynamic/pub_img.php";
    /**
     * 举报发图
     */
    public static final String JUBAO_IMG = URL + "/client/user/black/pub_img.php";
    /**
     * 发布约会视频
     */
    public static final String PUBLISH_VIDEO = URL + "/client/user/dynamic/pub_video.php";
    /**
     * 获取附近的人列表
     **/
    public static final String NearUserList = URL + "/client/user/nearby_user_list.php";
    /**
     * 个人动态
     **/
    public static final String PERSONALDONGTAI = URL + "/client/user/dynamic/user_dy_list.php";
    /**
     * 获取动态详情
     **/
    public static final String GETDONGTAI_DETAIL = URL + "/client/dynamic/dynamic_detail.php";
    /**
     * 评论
     **/
    public static final String REPLY_DONGTAI = URL + "/client/user/dynamic/reply_dy.php";
    /**
     * 删除留言板评论
     **/
    public static final String DELETE_REPLY = URL + "/client/user/dynamic/d_dyreply.php";
    /**
     * 获取支付宝单号
     **/
    public static final String GETALIPAY = URL + "/client/pay/build_alipay.php";
    /**
     * 举报
     **/
    public static final String JUBAO = URL + "/client/user/black/new_report.php";
    /**
     * 举报并拉黑
     **/
    public static final String JUBAOANDBLACK = URL + "/client/user/black/new_report_and_black.php";
    /**
     * 加入黑名单
     **/
    public static final String ADDTOBLACK = URL + "/client/user/black/add_black.php";
    /**
     * 取消黑名单
     **/
    public static final String CANCLEBLACK = URL + "/client/user/black/cancel_black.php";
    /**
     * 黑名单列表
     **/
    public static final String BLACKLIST = URL + "/client/user/black/black_list.php";
    /**
     * 粉丝和关注
     **/
    public static final String MYCAREANDFUNS = URL + "/client/user/atten/concern_list.php";
    /**
     * 发送语音图片
     **/
    public static final String SENDFILE = URL + "/client/msg/send_file_msg.php";
    /**
     * 发送视频
     */
    public static final String SENDVIDEO = URL + "/client/msg/send_videofile_msg.php";
    /**
     * 发送群语音图片
     */
    public static final String SENDGROUPFILE = URL + "/client/group/send_file_msg.php";
    /**
     * 发送群语音图片
     */
    public static final String SENDGROUPVIDEO = URL + "/client/group/send_videofile_msg.php";
    /**
     * 统计
     **/
    public static final String TONGJI = URL + "/reg_channel.php";
    /**
     * 会员漫游
     **/
    public static final String ROAMUSERLIST = URL + "/client/user/roam_list.php";
    /**
     * 上传相册图片
     **/
    public static final String UPLOAD_ALBUM_IMG = URL + "/client/user/img/album/upload.php";
    /**
     * 动态
     **/
    public static final String DONGTAI = URL + "/client/user/dynamic/dy_list.php";
    /**
     * 关注的人的约会
     **/
    public static final String MY_CARE_DATE = URL + "/client/attention/dynamic_list.php";
    /**
     * 是否有进行中的约会
     **/
    public static final String AFFIRMPUB = URL + "/client/user/dynamic/affirm_pub.php";
    /**
     * 收藏约会/取消收藏
     **/
    public static final String COLLECTDATE = URL + "/client/user/dynamic/collect_dynamic.php";
    /**
     * 收藏约会列表
     **/
    public static final String COLLECTLIST = URL + "/client/user/dynamic/my_coll_dylist.php";
    /**
     * 评论列表
     **/
    public static final String COMMETLIST = URL + "/client/dynamic/comm_list.php";
    /**
     * 访客记录
     **/
    public static final String VISITLIST = URL + "/client/user/visit/v_list.php";
    /**
     * 删除访客记录
     **/
    public static final String DELETE_VISITLIST = URL + "/client/user/visit/del_visit.php";
    /**
     * 查找好友
     **/
    public static final String SEARCH_FRIEND = URL + "/client/user/atten/find_new_friends.php";
    /**
     * 推荐好友
     **/
    public static final String MAYUSE_FRIEND = URL + "/client/user/push_user_list.php";
    /***
     * 表情下载列表
     */
    public static final String EMOTICON_DOWNLOAD_LIST = URL + "/client/user/phiz/get_phiz_list.php";
    /**
     * 表情详情
     */
    public static final String EMOTICON_DETAIL = URL + "/client/user/phiz/get_phiz_detail.php";
    /**
     * 下载表情
     */
    public static final String DOWNLOAD_EMOTICON_FIRST = URL + "/client/user/phiz/ready_down.php";
    /**
     * 表情管理
     */
    public static final String EMOCTION_MANAGE = URL + "/client/user/phiz/buy_phiz_list.php";
    /**
     * 获取用户计数
     */
    public static final String STATISTIC = URL + "/client/user/info_cnt.php";
    /**
     * 发送消息
     */
    public static final String SEND_MESSAGE = URL + "/client/msg/send_characters_msg.php";
    /**
     * 发送群组消息
     */
    public static final String SEND_GROUP_MESSAGE = URL + "/client/group/send_text_msg.php";
    /**
     * 微信支付
     */
    public static final String GETWXPAY = URL + "/client/wxpay/build_alipay.php";
    /**
     * 钱包支付
     */
    public static final String GETWALLETPAY = URL + "/client/user/user_pay.php";
    /**
     * 摇一摇
     */
    public static final String SHAKESWEEP = URL + "/client/sweep/sweep.php";
    /**
     * 约会--赞
     */
    public static final String DONGTAIZAN = URL + "/client/user/dynamic/change_zambia.php";
    /**
     * 约会推迟15天
     */
    public static final String DATEDELAY = URL + "/client/user/dynamic/delay_dynamic.php";
    /****
     * 关注
     ***/
    public static final String ADD_CARE = URL + "/client/user/atten/concern_he.php";
    /****
     * 创建群
     ***/
    public static final String ADD_GROUP = URL + "/client/group/add_group.php";
    /****
     * 上传群头像
     ***/
    public static final String UPLOAD_GROUPLOGO = URL + "/client/group/avatar_upload.php";
    /**
     * 群组列表
     */
    public static final String GROUP_LIST = URL + "/client/group/group_list.php";
    /**
     * 我的群组
     */
    public static final String My_GROUP_LIST = URL + "/client/group/mygroup_list.php";
    /**
     * 群组详情信息
     */
    public static final String GET_GROUP_DETAIL = URL + "/client/group/group_detail.php";
    /**
     * 76申请/邀请/退出/拒绝/踢出/解散
     */
    public static final String GROUP_MANAGER_USER = URL + "/client/group/msg_notify.php";
    /**
     * 群人员信息
     */
    public static final String GROUP_MEMBER_LIST = URL + "/client/group/group_member_list.php";
    /****
     * 编辑群信息
     ***/
    public static final String EDIT_GROUP = URL + "/client/group/edit_info.php";
    /****
     * 编辑群相册
     ***/
    public static final String EDIT_GROUP_ABLM = URL + "/client/group/img/avatar/update_num.php";
    /****
     * 上传群相册,替换群图片
     ***/
    public static final String UPLOAD_GROUP_ABLM = URL + "/client/group/upload.php";
    /****
     * 是否可以创建群组
     ***/
    public static final String CAN_CREATE_GROUP = URL + "/client/group/affirm_add.php";
    /**
     * 77同意申请/接受邀请
     */
    public static final String AGREE_GROUP_REQUEST = URL + "/client/member/add_member.php";

    /**
     * 获取用户头像昵称
     */
    public static final String GET_USER_SIMPLE_INFO = URL + "/client/user/name_avatar.php";
    /**
     * 获取群组头像昵称
     */
    public static final String GET_GROUP_SIMPLE_INFO = URL + "/client/group/name_avatar.php";
    /**
     * 获取联系计数
     */
    public static final String GET_RELATION_CNT = URL + "/client/user/relevant_cnt.php";
    /**
     * 邀请关注人列表
     **/
    public static final String GROUPMYCARE = URL + "/client/group/atten/concern_list.php";
    /**
     * 获取约会类型列表
     */
    public static final String GET_DATE_TYPE_LIST = URL + "/client/dynamic/dynamic_type.php";
    /**
     * 获取个人账户信息
     */
    public static final String GET_USER_ACCOUNT = URL + "/client/user/user_account.php";
    /**
     * 拆红包
     */
    public static final String OPEN_RED_PACKET = URL + "/client/red/get_redpacket.php";
    /**
     * 查看钱包明细
     */
    public static final String GET_PAYMENT_DETAIL = URL + "/client/user/user_bill.php";
    /**
     * 钱包提现
     */
    public static final String MONEY_TI_XIAN = URL + "/client/user/user_draw.php";
    /**
     * 上传真人认证视频和缩略图
     **/
    public static final String UPLOADTRUEAUTH = URL + "/client/confirm/add_new_confirm.php";
    /**
     * 查看认证视频详情
     **/
    public static final String VIDEODETAIL = URL + "/client/confirm/confirm_detail.php";
    /**
     * 认证视频回复列表
     **/
    public static final String VIDEOREPLY = URL + "/client/confirm/comm_list.php";
    /**
     * 评论/回复认证视频
     **/
    public static final String REPLYVIDEODETAIL = URL + "/client/report/reply_dy.php";
    /**
     * 删除认证视频评论
     **/
    public static final String DELETEVIDEOAUTHREPLY = URL + "/client/user/confirm/d_dyreply.php";
    /**
     * 删除认证视频
     **/
    public static final String DELETEVIDEOAUTH = URL + "/client/confirm/cancel_status.php";
    /**
     * 推荐的约会列表
     */
    public static final String HOT_DATE_LIST = URL + "/client/user/dynamic/push_list.php";
    /**
     * 人气列表
     */
    public static final String HOT_USER_LIST = URL + "/client/popularity/popularity_list.php";
    /**
     * 我的人气明细
     */
    public static final String MY_POPULAR_VALUE = URL + "/client/popularity/pop_bill_list.php";
    /**
     * 获取举报类型
     */
    public static final String JUBAO_LIST = URL + "/client/report/report_type.php";
    /**
     * 公告栏
     */
    public static final String NOTICE = URL + "/notice.php";
}
