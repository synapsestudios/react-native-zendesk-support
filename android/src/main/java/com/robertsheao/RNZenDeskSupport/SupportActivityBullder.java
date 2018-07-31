package com.robertsheao.RNZenDeskSupport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.feedback.WrappedZendeskFeedbackConfiguration;
import com.zendesk.sdk.feedback.ZendeskFeedbackConfiguration;
import com.zendesk.sdk.support.ContactUsButtonVisibility;
import com.zendesk.sdk.support.SupportActivity;
import com.zendesk.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Patrick O'Connor on 11/8/17.
 * This is a mostly a copy of Zendesk's SupportActivity.Builder.class, modified slightly to allow configuration from the React Native module.
 * It also adds the Intent.FLAG_ACTIVITY_NEW_TASK flag to the created Intent, fixing a crashing bug in older versions of Android.
 * https://github.com/RobertSheaO/react-native-zendesk-support
 */

class SupportActivityBuilder {
  private final Bundle args = new Bundle();

  private SupportActivityBuilder() {
  }

  private static long[] toLongArray(ArrayList<?> values) {
    long[] arr = new long[values.size()];
    for (int i = 0; i < values.size(); i++)
      arr[i] = Long.parseLong((String) values.get(i));
    return arr;
  }

  static SupportActivityBuilder create() {
    SupportActivityBuilder builder = new SupportActivityBuilder();
    builder.showConversationsMenuButton(true);
    builder.withArticleVoting(true);
    builder.withContactUsButtonVisibility(ContactUsButtonVisibility.ARTICLE_LIST_AND_ARTICLE);
    return builder;
  }

  SupportActivityBuilder withOptions(ReadableMap options) {
    if (!(options == null || options.toHashMap().isEmpty())) {
      if (options.hasKey("showConversationsMenuButton")) {
        this.showConversationsMenuButton(options.getBoolean("showConversationsMenuButton"));
      }
      if (options.hasKey("articleVotingEnabled")) {
        this.withArticleVoting(options.getBoolean("articleVotingEnabled"));
      }
      if (options.hasKey("withContactUsButtonVisibility")) {
        switch(options.getString("withContactUsButtonVisibility")) {
          case "OFF":
            withContactUsButtonVisibility(ContactUsButtonVisibility.OFF);
            break;
          case "ARTICLE_LIST_ONLY":
            withContactUsButtonVisibility(ContactUsButtonVisibility.ARTICLE_LIST_ONLY);
            break;
          case "ARTICLE_LIST_AND_ARTICLE":
          default:
            withContactUsButtonVisibility(ContactUsButtonVisibility.ARTICLE_LIST_AND_ARTICLE);
        }
      }
    }
    return this;
  }

  SupportActivityBuilder withArticlesForCategoryIds(ReadableArray categoryIds) {
    return withArticlesForCategoryIds(toLongArray(categoryIds.toArrayList()));
  }

  private SupportActivityBuilder withArticlesForCategoryIds(long... categoryIds) {
    if(this.args.getLongArray("extra_section_ids") != null) {
      Logger.w("SupportActivity", "Builder: sections have already been specified. Removing section IDs to set category IDs.", new Object[0]);
      this.args.remove("extra_section_ids");
    }

    this.args.putLongArray("extra_category_ids", categoryIds);
    return this;
  }

  SupportActivityBuilder withArticlesForSectionIds(ReadableArray sectionIds) {
    return withArticlesForSectionIds(toLongArray(sectionIds.toArrayList()));
  }

  private SupportActivityBuilder withArticlesForSectionIds(long... sectionIds) {
    if(this.args.getLongArray("extra_category_ids") != null) {
      Logger.w("SupportActivity", "Builder: categories have already been specified. Removing category IDs to set section IDs.", new Object[0]);
      this.args.remove("extra_category_ids");
    }

    this.args.putLongArray("extra_section_ids", sectionIds);
    return this;
  }

  /** @deprecated */
  SupportActivityBuilder showContactUsButton(boolean showContactUsButton) {
    this.args.putSerializable("extra_contact_us_button_visibility", showContactUsButton? ContactUsButtonVisibility.ARTICLE_LIST_ONLY:ContactUsButtonVisibility.OFF);
    return this;
  }

  private SupportActivityBuilder withContactUsButtonVisibility(ContactUsButtonVisibility contactUsButtonVisibility) {
    this.args.putSerializable("extra_contact_us_button_visibility", contactUsButtonVisibility);
    return this;
  }

  private SupportActivityBuilder withContactConfiguration(ZendeskFeedbackConfiguration configuration) {
    if(configuration != null) {
      configuration = new WrappedZendeskFeedbackConfiguration((ZendeskFeedbackConfiguration)configuration);
    }

    this.args.putSerializable("extra_contact_configuration", (Serializable)configuration);
    return this;
  }

  //noinspection SuspiciousToArrayCall
  SupportActivityBuilder withLabelNames(ReadableArray labelNames) {
    return withLabelNames(labelNames.toArrayList().toArray(new String[]{}));
  }

  private SupportActivityBuilder withLabelNames(String... labelNames) {
    if(CollectionUtils.isNotEmpty(labelNames)) {
      this.args.putStringArray("extra_label_names", labelNames);
    }

    return this;
  }

  private SupportActivityBuilder withCategoriesCollapsed(boolean categoriesCollapsed) {
    this.args.putBoolean("extra_categories_collapsed", categoriesCollapsed);
    return this;
  }

  private SupportActivityBuilder showConversationsMenuButton(boolean showConversationsMenuButton) {
    this.args.putBoolean("extra_show_conversations_menu_button", showConversationsMenuButton);
    return this;
  }

  private SupportActivityBuilder withArticleVoting(boolean articleVotingEnabled) {
    this.args.putBoolean("article_voting_enabled", articleVotingEnabled);
    return this;
  }

  void show(Context context) {
    Logger.d("SupportActivity", "show: showing SupportActivity", new Object[0]);
    context.startActivity(this.intent(context));
  }

  private Intent intent(Context context) {
    Logger.d("SupportActivity", "intent: creating Intent", new Object[0]);
    Intent intent = new Intent(context, SupportActivity.class);
    intent.putExtras(this.args);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }
}
