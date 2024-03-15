# UserAPI

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createUser**](UserAPI.md#createuser) | **POST** /user | 
[**deleteUser**](UserAPI.md#deleteuser) | **DELETE** /user/{id} | 
[**getManyUsers**](UserAPI.md#getmanyusers) | **GET** /user | 
[**getUser**](UserAPI.md#getuser) | **GET** /user/{id} | 
[**updateUser**](UserAPI.md#updateuser) | **PATCH** /user | 


# **createUser**
```swift
    open class func createUser(userDto: UserDto, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let userDto = UserDto(id: 123, firstname: "firstname_example", lastname: "lastname_example", email: "email_example", gender: "gender_example", googleToken: "googleToken_example", facebookToken: "facebookToken_example", appleToken: "appleToken_example", activities: ["activities_example"], media: ["media_example"], friends: ["friends_example"], interests: ["interests_example"]) // UserDto | 

// 
UserAPI.createUser(userDto: userDto) { (response, error) in
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
 **userDto** | [**UserDto**](UserDto.md) |  | 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteUser**
```swift
    open class func deleteUser(id: Double, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let id = 987 // Double | 

// 
UserAPI.deleteUser(id: id) { (response, error) in
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

# **getManyUsers**
```swift
    open class func getManyUsers(sortType: String? = nil, page: Double? = nil, limit: Double? = nil, sortBy: [String]? = nil, searchBy: String? = nil, completion: @escaping (_ data: [UserDto]?, _ error: Error?) -> Void)
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
UserAPI.getManyUsers(sortType: sortType, page: page, limit: limit, sortBy: sortBy, searchBy: searchBy) { (response, error) in
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

[**[UserDto]**](UserDto.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUser**
```swift
    open class func getUser(id: Double, completion: @escaping (_ data: UserDto?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let id = 987 // Double | 

// 
UserAPI.getUser(id: id) { (response, error) in
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

[**UserDto**](UserDto.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateUser**
```swift
    open class func updateUser(userUpdateDto: UserUpdateDto, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let userUpdateDto = UserUpdateDto(id: 123, firstname: "firstname_example", lastname: "lastname_example", email: "email_example", gender: "gender_example", googleToken: "googleToken_example", facebookToken: "facebookToken_example", appleToken: "appleToken_example", birthday: Date(), bio: "bio_example", _public: false, subCategoryIds: [123], location: "location_example") // UserUpdateDto | User model for update

// 
UserAPI.updateUser(userUpdateDto: userUpdateDto) { (response, error) in
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
 **userUpdateDto** | [**UserUpdateDto**](UserUpdateDto.md) | User model for update | 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

