package com.festum.festumfield.Utils;

public class Constans {

    public static String CHAT_SERVER_URL = "https://api.festumfield.com";

    public static String SOCKET_SERVER_URL = "https://api.festumfield.com";

    public static String Display_Image_URL = "https://festumfield.s3.ap-south-1.amazonaws.com/";

    public static String BASE_URL = "https://api.festumfield.com/apis/v1/";

    //Login
    public static String send_otp = BASE_URL + "register/sendotp";
    public static String verify_otp = BASE_URL + "register/verifyotp";
    public static String change_number = BASE_URL + "register/changenumber";
    public static String verify_otp_new = BASE_URL + "register/verifyotpfornewnumber";

    //Personal Profile
    public static String profile_register = BASE_URL + "profile/setprofile";
    public static String fetch_personal_info = BASE_URL + "profile/getprofile";
    public static String set_profile_pic = BASE_URL + "profile/setprofilepic";

    public static String set_group_pic = BASE_URL + "group/uploadgroupprofileimage";

    //Business Profile
    public static String business_register = BASE_URL + "business/setbusiness";
    public static String fetch_business_info = BASE_URL + "business/getbusiness";
    public static String set_business_profile_pic = BASE_URL + "business/setbusinessprofile";
    public static String set_Setbrochure_pdf = BASE_URL + "business/setbrochure";

    //Product
    public static String set_product_image = BASE_URL + "product/uploadimage";
    public static String add_product = BASE_URL + "product/create";
    public static String update_product = BASE_URL + "product/edit";
    public static String list_product = BASE_URL + "product/list";
    public static String remove_product = BASE_URL + "product/remove";
    public static String fetch_single_product = BASE_URL + "product/single";

    public static String get_friends_product_list = BASE_URL + "product/friendsproducts";

    //Notification
    public static String create_notification = BASE_URL + "notification/create";
    public static String update_notification = BASE_URL + "notification/edit";
    public static String fetch_notification = BASE_URL + "notification/list";
    public static String fetch_single_notification = BASE_URL + "notification/single";
    public static String remove_notification = BASE_URL + "notification/remove";
    public static String set_notification_banner = BASE_URL + "notification/setnotificationbanner";

    //Location
    public static String set_find_friends_location_or_name = BASE_URL + "friends/findfriends";

    //Friend Request
    public static String set_new_friend_request = BASE_URL + "friends/sendfriendrequest";
    public static String response_friend_request = BASE_URL + "friends/updatefriendrequest";
    public static String all_recived_friend_request = BASE_URL + "friends/receivedfriendrequests";
    public static String all_send_friend_request = BASE_URL + "friends/sentfriendrequests";
    public static String all_myfriend = BASE_URL + "friends/myfriends";
    public static String all_block_friends = BASE_URL + "friends/blockedrequest";
    public static String set_authorized_permissions = BASE_URL + "friends/set_authorized_permissions";
    public static String set_friends_unfriendorblock = BASE_URL + "friends/unfriendorblock";

    public static String set_friends_unblock = BASE_URL + "friends/unblock";

    //Chat
    public static String set_chat_message = BASE_URL + "chats/send";
    public static String list_chat_message = BASE_URL + "chats";

    public static String marketing_notification = BASE_URL + "marketing/notification";
    public static String story_create = BASE_URL + "story";
    public static String story_increase_view_count = BASE_URL + "story/increase-view-count/";
    public static String contact_us = BASE_URL + "contact-us";

}
