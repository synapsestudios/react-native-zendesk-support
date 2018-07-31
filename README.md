# react-native-zendesk-support
React Native bridge to Zendesk Support SDK on iOS and Android. This currently only supports using the out of the box views the Zendesk Support SDK provides. At the moment, only anonymous authentication is supported.

## React Native Version Support

This has only been tested to work with React Native 0.47, probably works in earlier versions.

## Getting started

### Installing via RNPM (Common)
```
react-native link react-native-zendesk-support
```

### Installing via Cocoapods (Not Common)

Add the following line to your Podfile:

###### ios/Podfile
```
pod 'react-native-zendesk-support', :path => '../node_modules/react-native-zendesk-support'

post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      target.build_settings(config.name)['CLANG_ALLOW_NON_MODULAR_INCLUDES_IN_FRAMEWORK_MODULES'] = 'YES'
    end
  end
end
```

### Manually Linking (iOS)

If using `react-native link` doesn't work, you can try manually linking. Sometimes apps created with `create-react-native-app` that haven't been ejected can have problems linking properly.

1. Open your project in XCode, right click on `Libraries` and click `Add Files to "Your Project Name"`. Look under node_modules/react-native-zendesk-support` and add `RNZenDeskSupport.xcodeproj`
2. Add `libRNZenDeskSupport.a` from `Libraries/RNZenDeskSupport.xcodeproj/Products` to `Build Phases -> Link Binary With Libraries`
3. Verify `$(SRCROOT)/../../react-native/React` is included in `Header Search Paths` under `Build Settings` for the `Libraries/RNZenDeskSupport.xcodeproj` library you just added. Mark it as `recursive`


### Manually Linking (Android)

###### android/app/build.gradle
```diff
dependencies {
    ...
    compile "com.facebook.react:react-native:+"  // From node_modules
+   compile project(':react-native-zendesk-support')
}
```

###### android/settings.gradle
```diff
...
include ':app'
+ include ':react-native-zendesk-support'
+ project(':react-native-zendesk-support').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-zendesk-support/android')
```

### Configure Android (Must Do)
You need to add the following repository to your `android/app/build.gradle` file. If you do not already have a `repositories` section, add it at the root level of the file right before the `dependencies` section.

###### android/app/build.gradle
```
repositories {
    maven { url 'https://zendesk.jfrog.io/zendesk/repo' }
}
```

###### MainApplication.java
```diff
+ import com.robertsheao.RNZenDeskSupport.RNZenDeskSupport;

  public class MainApplication extends Application implements ReactApplication {
    //......

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
+         new RNZenDeskSupport(),
          new MainReactPackage()
      );
    }

    ......
  }
```

### Configure iOS (Must Do)
You need to follow the instructions to integrate the Zendesk Support SDK for [iOS](https://developer.zendesk.com/embeddables/docs/ios/integrate_sdk).

Personally, I use the CocoaPods implementation described in their documentation.

## Usage

#### Import the module
```js
import ZendeskSupport from 'react-native-zendesk-support';
```

#### Initialize Zendesk
```js
const config = {
  appId: 'your_app_id',
  zendeskUrl: 'your_zendesk_url',
  clientId: 'your_client_id'
}
ZendeskSupport.initialize(config)
```
###### Note: You must initialize Zendesk prior to calling setupIdentity. Best place for it would be inside `componentWillMount`

#### Define an identity
```js
// passing an identity to setupIdentity() is optional, pass null instead
const identity = {
  customerEmail: 'foo@bar.com',
  customerName: 'Foo Bar'
}
ZendeskSupport.setupIdentity(identity)
```
###### Note: You must define an identity prior to calling any support ticket or help center methods. Suggested places are inside `componentWillMount` or `componentWillReceiveProps` if your identity details aren't immediately available

### Support Tickets

#### File a ticket
```js
const customFields = {
  customFieldId: 'Custom Field Value'
}
ZendeskSupport.callSupport(customFields)
```

#### Bring up ticket history
```js
ZendeskSupport.supportHistory()
```

### Help Center

#### Show help center
```js
ZendeskSupport.showHelpCenter()
```

#### Show categories, e.g., FAQ
```js
ZendeskSupport.showCategories(['categoryId'])
```

#### Show sections, e.g., Account Questions
```js
ZendeskSupport.showSections(['sectionId'])
```

#### Show labels, e.g., tacocat
```js
ZendeskSupport.showLabels(['tacocat'])
```

#### Options
The Help Center functions above support a second parameter, an object of options.
```js
const options = {
  articleVotingEnabled: false,
  hideContactSupport: false,
  showConversationsMenuButton: false,
  withContactUsButtonVisibility: 'OFF'
}
ZendeskSupport.showHelpCenterWithOptions({ options })
ZendeskSupport.showCategoriesWithOptions(['categoryId'], { options })
ZendeskSupport.showSectionsWithOptions(['sectionId'], { options })
ZendeskSupport.showLabelsWithOptions(['tacocat'], { options })
```

##### articleVotingEnabled _boolean_
* **true** _(default)_ – Show voting buttons on articles
* **false** – Hide voting buttons on articles

##### hideContactSupport _boolean_
* **true** _(default)_ – Shows contact support option in empty results and navigation bar on iOS
* **false** – Hides contact support option in empty results and navigation bar on iOS

##### showConversationsMenuButton _boolean_
* **true** _(default)_ – Shows the right menu on Android which shows tickets
* **false** – Hides the right menu on Android which shows tickets

##### withContactUsButtonVisibility _string (case sensitive)_
* **ARTICLE_LIST_AND_ARTICLE** _(default)_ – Show floating action button in list and article view
* **ARTICLE_LIST_ONLY** – Show floating action button only in list views
* **OFF** – Hide floating action button on articles and list views

### Styling Category Headers (Android Only)
There is an out of the box issue with Zendesk SDK, as reported by Zendesk support staff themselves, where the expanded category headers use the same color as the top header. Unfortunately, the default top header color and the background color are very close and you can barely tell the text is even there when the category is expanded.

You're gonna need to update your `android/app/src/main/res/values/styles.xml` to extend from the ZendeskSdkTheme to define your own colors. Below is my own, you can change it to whatever you want your primary color to be.

###### android/app/src/main/res/values/styles.xml
```xml
<resources>
    <style name="AppTheme" parent="ZendeskSdkTheme.Light">
      <item name="colorPrimary">#FF6240</item>
    </style>
</resources>
```

If you're interested in other things you can theme, or if you want to implement themes differently in Android, you can check out the [Zendesk SDK documention](https://developer.zendesk.com/embeddables/docs/android/customize_the_look).

## Troubleshooting
#### Help Center has no content
First off, make sure your content is published. Secondly, you need to make sure to "Enable Guide" in your Zendesk settings so the content will appear. It is described under [Enabling Help Center in setup mode](https://support.zendesk.com/hc/en-us/articles/203664346-Getting-started-with-Guide-Setting-up#ariaid-title5) in the official Zendesk Support documentation.

#### Help Center says "Failed to get categories"
You need to call `ZendeskSupport.setupIdentity` before calling help center.

#### Zendesk doesn't open for filing/viewing tickets or showing Help Center
You need to call `ZendeskSupport.initialize` before calling any other methods.

#### Custom Fields data doesn't appear in Zendesk agent dashboard
Custom fields need to be set to both "Visible" and "Editable" inside the Zendesk admin console.

## Upcoming Features
* Authenticate using JWT endpoint
* Theme support (iOS only)
* Show article by id
* Hiding "Contact us" on iOS from article and list view
