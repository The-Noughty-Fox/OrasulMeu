# AuthAPI

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**authenticateApple**](AuthAPI.md#authenticateapple) | **POST** /auth/apple | 
[**authenticateGoogle**](AuthAPI.md#authenticategoogle) | **POST** /auth/google | 
[**authenticateWithFacebookPo**](AuthAPI.md#authenticatewithfacebookpo) | **POST** /auth/facebook | 


# **authenticateApple**
```swift
    open class func authenticateApple(appleToken: AppleToken, completion: @escaping (_ data: BaseUserDto?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let appleToken = appleToken(authorizationCode: "authorizationCode_example") // AppleToken | 

AuthAPI.authenticateApple(appleToken: appleToken) { (response, error) in
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
 **appleToken** | [**AppleToken**](AppleToken.md) |  | 

### Return type

[**BaseUserDto**](BaseUserDto.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **authenticateGoogle**
```swift
    open class func authenticateGoogle(token: Token, completion: @escaping (_ data: BaseUserDto?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let token = token(token: "token_example") // Token | 

AuthAPI.authenticateGoogle(token: token) { (response, error) in
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
 **token** | [**Token**](Token.md) |  | 

### Return type

[**BaseUserDto**](BaseUserDto.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **authenticateWithFacebookPo**
```swift
    open class func authenticateWithFacebookPo(token: Token, completion: @escaping (_ data: BaseUserDto?, _ error: Error?) -> Void)
```



### Example
```swift
// The following code samples are still beta. For any issue, please report via http://github.com/OpenAPITools/openapi-generator/issues/new
import API

let token = token(token: "token_example") // Token | 

AuthAPI.authenticateWithFacebookPo(token: token) { (response, error) in
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
 **token** | [**Token**](Token.md) |  | 

### Return type

[**BaseUserDto**](BaseUserDto.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

