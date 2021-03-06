package com.restaurantsapp.webservice;

import android.content.Context;
import android.text.TextUtils;

import com.restaurantsapp.model.GalleryModel;
import com.restaurantsapp.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Alg on 29/05/21.
 */
public class WSUpdateRestaurantNew {
    private boolean isError;
    private boolean isLogout;
    private String message;
    private Context context;


    public boolean isError() {
        return isError;
    }

    public boolean isLogout() {
        return isLogout;
    }

    public WSUpdateRestaurantNew(final Context context) {
        this.context = context;
    }


    public String getMessage() {
        return message;
    }


    private RequestBody addRestaurant(final String restaurant_name, final String restaurantID, final ArrayList<GalleryModel> bannerArrayList, final ArrayList<GalleryModel> menuArrayList) {
        final WSConstant wsConstant = new WSConstant();
        final MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        final PreferenceUtils preferenceUtils = new PreferenceUtils(context);
        final String userId = preferenceUtils.getString(PreferenceUtils.USER_ID);
        final String token = preferenceUtils.getString(PreferenceUtils.TOKEN);
        final String contactNumber = preferenceUtils.getString(PreferenceUtils.USER_CONTACT_NUMBER);
        builder.addFormDataPart(wsConstant.PARAMS_RESTAURANT_NAME, restaurant_name);
        builder.addFormDataPart(wsConstant.PARAMS_TOKEN, token);
        builder.addFormDataPart(wsConstant.PARAMS_USER_ID, userId);
        builder.addFormDataPart(wsConstant.PARAMS_RESTAURANT_ID, restaurantID);
        builder.addFormDataPart(wsConstant.PARAMS_CONTACT_NUMBER, contactNumber);

        for (int i = 0; i < bannerArrayList.size(); i++) {
            final GalleryModel galleryModel = bannerArrayList.get(i);
            final String imagePath = galleryModel.getImagePath();
            final File file = new File(imagePath);
            if (!TextUtils.isEmpty(imagePath) && file != null && file.exists()) {
                builder.addFormDataPart(wsConstant.PARAMS_RESTAURANTS_BANNER_ + (i + 1), imagePath,
                        RequestBody.create(WSConstant.MEDIA_TYPE_JPG, file));
            }
        }

        for (int i = 0; i < menuArrayList.size(); i++) {
            final GalleryModel galleryModel = menuArrayList.get(i);
            final String imagePath = galleryModel.getImagePath();
            final File file = new File(imagePath);
            if (!TextUtils.isEmpty(imagePath) && file != null && file.exists()) {
                builder.addFormDataPart(wsConstant.PARAMS_RESTAURANTS_MENU_IMAGES_ + (i + 1), imagePath,
                        RequestBody.create(WSConstant.MEDIA_TYPE_JPG, file));
            }
        }


        final RequestBody formBody = builder.build();
        return formBody;
    }


    public void executeService(final String restaurant_name, final String restaurantID, final ArrayList<GalleryModel> bannerArrayList, final ArrayList<GalleryModel> menuArrayList) {
        final WSUtil wsUtil = new WSUtil();
        final String url = WSConstant.BASE_URL + WSConstant.METHOD_UPDATE_RESTAURANT;
        final String response = wsUtil.wsPost(url, addRestaurant(restaurant_name, restaurantID, bannerArrayList, menuArrayList));
        parseResponse(response);
    }

    private void parseResponse(final String response) {
        try {
            final JSONObject jsonObject = new JSONObject(response);
            final WSConstant wsConstant = new WSConstant();
            isError = jsonObject.optBoolean(wsConstant.PARAMS_ERROR);
            isLogout = jsonObject.optBoolean(wsConstant.PARAMS_IS_LOGOUT);
            message = jsonObject.optString(wsConstant.PARAMS_ERROR_MSG);

            if (isError) {
                final JSONArray jsonArray = jsonObject.getJSONArray(wsConstant.PARAMS_DATA);
                final JSONObject jsonObjectData = jsonArray.getJSONObject(0);
                final int userId = jsonObjectData.getInt(wsConstant.PARAMS_USER_ID);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
