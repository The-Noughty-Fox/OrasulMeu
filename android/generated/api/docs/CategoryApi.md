# CategoryAPI

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createCategory**](CategoryAPI.md#createcategory) | **POST** /category | 
[**deleteCategory**](CategoryAPI.md#deletecategory) | **DELETE** /category/{id} | 
[**getCategory**](CategoryAPI.md#getcategory) | **GET** /category/{id} | 
[**getManyCategories**](CategoryAPI.md#getmanycategories) | **GET** /category | 
[**updateCategory**](CategoryAPI.md#updatecategory) | **PATCH** /category | 


# **createCategory**
```swift
    open class func createCategory(categoryDto: CategoryDto, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let categoryDto = CategoryDto(id: 123, name: "name_example", description: "description_example") // CategoryDto | 

// 
CategoryAPI.createCategory(categoryDto: categoryDto) { (response, error) in
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
 **categoryDto** | [**CategoryDto**](CategoryDto.md) |  | 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteCategory**
```swift
    open class func deleteCategory(id: Double, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let id = 987 // Double | 

// 
CategoryAPI.deleteCategory(id: id) { (response, error) in
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

# **getCategory**
```swift
    open class func getCategory(id: Double, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let id = 987 // Double | 

// 
CategoryAPI.getCategory(id: id) { (response, error) in
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

# **getManyCategories**
```swift
    open class func getManyCategories(sortType: String? = nil, page: Double? = nil, limit: Double? = nil, sortBy: [String]? = nil, searchBy: String? = nil, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
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
CategoryAPI.getManyCategories(sortType: sortType, page: page, limit: limit, sortBy: sortBy, searchBy: searchBy) { (response, error) in
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

# **updateCategory**
```swift
    open class func updateCategory(categoryDto: CategoryDto, completion: @escaping (_ data: Void?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let categoryDto = CategoryDto(id: 123, name: "name_example", description: "description_example") // CategoryDto | 

// 
CategoryAPI.updateCategory(categoryDto: categoryDto) { (response, error) in
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
 **categoryDto** | [**CategoryDto**](CategoryDto.md) |  | 

### Return type

Void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

