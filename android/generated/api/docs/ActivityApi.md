# ActivityAPI

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createActivity**](ActivityAPI.md#createactivity) | **POST** /activity | 
[**deleteActivity**](ActivityAPI.md#deleteactivity) | **DELETE** /activity/{id} | 
[**getActivity**](ActivityAPI.md#getactivity) | **GET** /activity/{id} | 
[**getManyActivities**](ActivityAPI.md#getmanyactivities) | **GET** /activity | 
[**updateActivity**](ActivityAPI.md#updateactivity) | **PATCH** /activity | 


# **createActivity**
```swift
    open class func createActivity(activityDto: ActivityDto, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let activityDto = ActivityDto(id: 123, name: "name_example", createdBy: UserDto(id: 123, firstname: "firstname_example", lastname: "lastname_example", email: "email_example", gender: "gender_example", googleToken: "googleToken_example", facebookToken: "facebookToken_example", appleToken: "appleToken_example", activities: ["activities_example"], media: ["media_example"], friends: ["friends_example"], interests: ["interests_example"]), date: Date(), maxParticipants: 123, minParticipants: 123, participants: ["participants_example"]) // ActivityDto | 

// 
ActivityAPI.createActivity(activityDto: activityDto) { (response, error) in
    guard error == nil else {
        print(error)
        return
    }

    if (response) {
        dump(response)
    }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **activityDto** | [**ActivityDto**](ActivityDto.md) |  | 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteActivity**
```swift
    open class func deleteActivity(id: Double, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let id = 987 // Double | 

// 
ActivityAPI.deleteActivity(id: id) { (response, error) in
    guard error == nil else {
        print(error)
        return
    }

    if (response) {
        dump(response)
    }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Double** |  | 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getActivity**
```swift
    open class func getActivity(id: Double, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let id = 987 // Double | 

// 
ActivityAPI.getActivity(id: id) { (response, error) in
    guard error == nil else {
        print(error)
        return
    }

    if (response) {
        dump(response)
    }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Double** |  | 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getManyActivities**
```swift
    open class func getManyActivities(sortType: String? = nil, page: Double? = nil, limit: Double? = nil, sortBy: [String]? = nil, searchBy: String? = nil, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let sortType = "sortType_example" // String |  (optional)
let page = 987 // Double |  (optional) (default to 0)
let limit = 987 // Double |  (optional) (default to 10)
let sortBy = ["inner_example"] // [String] |  (optional)
let searchBy = "searchBy_example" // String |  (optional)

// 
ActivityAPI.getManyActivities(sortType: sortType, page: page, limit: limit, sortBy: sortBy, searchBy: searchBy) { (response, error) in
    guard error == nil else {
        print(error)
        return
    }

    if (response) {
        dump(response)
    }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sortType** | **String** |  | [optional] 
 **page** | **Double** |  | [optional] [default to 0]
 **limit** | **Double** |  | [optional] [default to 10]
 **sortBy** | [**[String]**](String.md) |  | [optional] 
 **searchBy** | **String** |  | [optional] 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateActivity**
```swift
    open class func updateActivity(activityDto: ActivityDto, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let activityDto = ActivityDto(id: 123, name: "name_example", createdBy: UserDto(id: 123, firstname: "firstname_example", lastname: "lastname_example", email: "email_example", gender: "gender_example", googleToken: "googleToken_example", facebookToken: "facebookToken_example", appleToken: "appleToken_example", activities: ["activities_example"], media: ["media_example"], friends: ["friends_example"], interests: ["interests_example"]), date: Date(), maxParticipants: 123, minParticipants: 123, participants: ["participants_example"]) // ActivityDto | 

// 
ActivityAPI.updateActivity(activityDto: activityDto) { (response, error) in
    guard error == nil else {
        print(error)
        return
    }

    if (response) {
        dump(response)
    }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **activityDto** | [**ActivityDto**](ActivityDto.md) |  | 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

