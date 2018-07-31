/**
 * Created by Patrick O'Connor on 8/30/17.
 * https://github.com/RobertSheaO/react-native-zendesk-support
 */

package com.robertsheao.RNZenDeskSupport;

import android.content.Intent;
import android.app.Activity;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.requests.RequestActivity;
import com.zendesk.sdk.support.SupportActivity;
import com.zendesk.sdk.support.ContactUsButtonVisibility;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.model.access.Identity;
import com.zendesk.sdk.model.request.CustomField;
import com.zendesk.sdk.network.impl.ZendeskConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RNZenDeskSupportModule extends ReactContextBaseJavaModule {
  public RNZenDeskSupportModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "RNZenDeskSupport";
  }

  private static long[] toLongArray(ArrayList<?> values) {
    long[] arr = new long[values.size()];
    for (int i = 0; i < values.size(); i++)
      arr[i] = Long.parseLong((String) values.get(i));
    return arr;
  }

  @ReactMethod
  public void initialize(ReadableMap config) {
    String appId = config.getString("appId");
    String zendeskUrl = config.getString("zendeskUrl");
    String clientId = config.getString("clientId");
    ZendeskConfig.INSTANCE.init(getReactApplicationContext(), zendeskUrl, appId, clientId);
  }

  @ReactMethod
    public void setupIdentity(ReadableMap identity) {
      AnonymousIdentity.Builder builder = new AnonymousIdentity.Builder();

      if (identity != null && identity.hasKey("customerEmail")) {
        builder.withEmailIdentifier(identity.getString("customerEmail"));
      }

      if (identity != null && identity.hasKey("customerName")) {
        builder.withNameIdentifier(identity.getString("customerName"));
      }

      ZendeskConfig.INSTANCE.setIdentity(builder.build());
    }

  @ReactMethod
  public void showHelpCenterWithOptions(ReadableMap options) {
    SupportActivityBuilder.create()
      .withOptions(options)
      .show(getReactApplicationContext());
  }

  @ReactMethod
  public void showCategoriesWithOptions(ReadableArray categoryIds, ReadableMap options) {
    SupportActivityBuilder.create()
      .withOptions(options)
      .withArticlesForCategoryIds(categoryIds)
      .show(getReactApplicationContext());
  }

  @ReactMethod
  public void showSectionsWithOptions(ReadableArray sectionIds, ReadableMap options) {
    SupportActivityBuilder.create()
      .withOptions(options)
      .withArticlesForSectionIds(sectionIds)
      .show(getReactApplicationContext());
  }

  @ReactMethod
  public void showLabelsWithOptions(ReadableArray labels, ReadableMap options) {
    SupportActivityBuilder.create()
      .withOptions(options)
      .withLabelNames(labels)
      .show(getReactApplicationContext());
  }

  @ReactMethod
  public void showHelpCenter() {
    showHelpCenterWithOptions(null);
  }

  @ReactMethod
  public void showCategories(ReadableArray categoryIds) {
    showCategoriesWithOptions(categoryIds, null);
  }

  @ReactMethod
  public void showSections(ReadableArray sectionIds) {
    showSectionsWithOptions(sectionIds, null);
  }

  @ReactMethod
  public void showLabels(ReadableArray labels) {
    showLabelsWithOptions(labels, null);
  }

  @ReactMethod
  public void callSupport(ReadableMap customFields) {

    List<CustomField> fields = new ArrayList<>();

    for (Map.Entry<String, Object> next : customFields.toHashMap().entrySet())
      fields.add(new CustomField(Long.parseLong(next.getKey()), (String) next.getValue()));

    ZendeskConfig.INSTANCE.setCustomFields(fields);

    Activity activity = getCurrentActivity();

    if(activity != null){
        Intent callSupportIntent = new Intent(getReactApplicationContext(), ContactZendeskActivity.class);
        callSupportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(callSupportIntent);
    }
  }

  @ReactMethod
  public void supportHistory() {

    Activity activity = getCurrentActivity();

    if(activity != null){
        Intent supportHistoryIntent = new Intent(getReactApplicationContext(), RequestActivity.class);
        supportHistoryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(supportHistoryIntent);
    }
  }

}
