//
// ActivityAPI.swift
//
// Generated by openapi-generator
// https://openapi-generator.tech
//

import Foundation
#if canImport(AnyCodable)
import AnyCodable
#endif

open class ActivityAPI {

    /**
     
     
     - parameter activityDto: (body)  
     - returns: Void
     */
    @available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 6.0, *)
    open class func createActivity(activityDto: ActivityDto) async throws {
        return try await createActivityWithRequestBuilder(activityDto: activityDto).execute().body
    }

    /**
     
     - POST /activity
     - parameter activityDto: (body)  
     - returns: RequestBuilder<Void> 
     */
    open class func createActivityWithRequestBuilder(activityDto: ActivityDto) -> RequestBuilder<Void> {
        let localVariablePath = "/activity"
        let localVariableURLString = APIAPI.basePath + localVariablePath
        let localVariableParameters = JSONEncodingHelper.encodingParameters(forEncodableObject: activityDto)

        let localVariableUrlComponents = URLComponents(string: localVariableURLString)

        let localVariableNillableHeaders: [String: Any?] = [
            :
        ]

        let localVariableHeaderParameters = APIHelper.rejectNilHeaders(localVariableNillableHeaders)

        let localVariableRequestBuilder: RequestBuilder<Void>.Type = APIAPI.requestBuilderFactory.getNonDecodableBuilder()

        return localVariableRequestBuilder.init(method: "POST", URLString: (localVariableUrlComponents?.string ?? localVariableURLString), parameters: localVariableParameters, headers: localVariableHeaderParameters, requiresAuthentication: false)
    }

    /**
     
     
     - parameter id: (path)  
     - returns: Void
     */
    @available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 6.0, *)
    open class func deleteActivity(id: Double) async throws {
        return try await deleteActivityWithRequestBuilder(id: id).execute().body
    }

    /**
     
     - DELETE /activity/{id}
     - parameter id: (path)  
     - returns: RequestBuilder<Void> 
     */
    open class func deleteActivityWithRequestBuilder(id: Double) -> RequestBuilder<Void> {
        var localVariablePath = "/activity/{id}"
        let idPreEscape = "\(APIHelper.mapValueToPathItem(id))"
        let idPostEscape = idPreEscape.addingPercentEncoding(withAllowedCharacters: .urlPathAllowed) ?? ""
        localVariablePath = localVariablePath.replacingOccurrences(of: "{id}", with: idPostEscape, options: .literal, range: nil)
        let localVariableURLString = APIAPI.basePath + localVariablePath
        let localVariableParameters: [String: Any]? = nil

        let localVariableUrlComponents = URLComponents(string: localVariableURLString)

        let localVariableNillableHeaders: [String: Any?] = [
            :
        ]

        let localVariableHeaderParameters = APIHelper.rejectNilHeaders(localVariableNillableHeaders)

        let localVariableRequestBuilder: RequestBuilder<Void>.Type = APIAPI.requestBuilderFactory.getNonDecodableBuilder()

        return localVariableRequestBuilder.init(method: "DELETE", URLString: (localVariableUrlComponents?.string ?? localVariableURLString), parameters: localVariableParameters, headers: localVariableHeaderParameters, requiresAuthentication: false)
    }

    /**
     
     
     - parameter id: (path)  
     - returns: Void
     */
    @available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 6.0, *)
    open class func getActivity(id: Double) async throws {
        return try await getActivityWithRequestBuilder(id: id).execute().body
    }

    /**
     
     - GET /activity/{id}
     - parameter id: (path)  
     - returns: RequestBuilder<Void> 
     */
    open class func getActivityWithRequestBuilder(id: Double) -> RequestBuilder<Void> {
        var localVariablePath = "/activity/{id}"
        let idPreEscape = "\(APIHelper.mapValueToPathItem(id))"
        let idPostEscape = idPreEscape.addingPercentEncoding(withAllowedCharacters: .urlPathAllowed) ?? ""
        localVariablePath = localVariablePath.replacingOccurrences(of: "{id}", with: idPostEscape, options: .literal, range: nil)
        let localVariableURLString = APIAPI.basePath + localVariablePath
        let localVariableParameters: [String: Any]? = nil

        let localVariableUrlComponents = URLComponents(string: localVariableURLString)

        let localVariableNillableHeaders: [String: Any?] = [
            :
        ]

        let localVariableHeaderParameters = APIHelper.rejectNilHeaders(localVariableNillableHeaders)

        let localVariableRequestBuilder: RequestBuilder<Void>.Type = APIAPI.requestBuilderFactory.getNonDecodableBuilder()

        return localVariableRequestBuilder.init(method: "GET", URLString: (localVariableUrlComponents?.string ?? localVariableURLString), parameters: localVariableParameters, headers: localVariableHeaderParameters, requiresAuthentication: false)
    }

    /**
     
     
     - parameter sortType: (query)  (optional)
     - parameter page: (query)  (optional, default to 0)
     - parameter limit: (query)  (optional, default to 10)
     - parameter sortBy: (query)  (optional)
     - parameter searchBy: (query)  (optional)
     - returns: Void
     */
    @available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 6.0, *)
    open class func getManyActivities(sortType: String? = nil, page: Double? = nil, limit: Double? = nil, sortBy: [String]? = nil, searchBy: String? = nil) async throws {
        return try await getManyActivitiesWithRequestBuilder(sortType: sortType, page: page, limit: limit, sortBy: sortBy, searchBy: searchBy).execute().body
    }

    /**
     
     - GET /activity
     - parameter sortType: (query)  (optional)
     - parameter page: (query)  (optional, default to 0)
     - parameter limit: (query)  (optional, default to 10)
     - parameter sortBy: (query)  (optional)
     - parameter searchBy: (query)  (optional)
     - returns: RequestBuilder<Void> 
     */
    open class func getManyActivitiesWithRequestBuilder(sortType: String? = nil, page: Double? = nil, limit: Double? = nil, sortBy: [String]? = nil, searchBy: String? = nil) -> RequestBuilder<Void> {
        let localVariablePath = "/activity"
        let localVariableURLString = APIAPI.basePath + localVariablePath
        let localVariableParameters: [String: Any]? = nil

        var localVariableUrlComponents = URLComponents(string: localVariableURLString)
        localVariableUrlComponents?.queryItems = APIHelper.mapValuesToQueryItems([
            "sortType": (wrappedValue: sortType?.encodeToJSON(), isExplode: true),
            "page": (wrappedValue: page?.encodeToJSON(), isExplode: true),
            "limit": (wrappedValue: limit?.encodeToJSON(), isExplode: true),
            "sortBy": (wrappedValue: sortBy?.encodeToJSON(), isExplode: true),
            "searchBy": (wrappedValue: searchBy?.encodeToJSON(), isExplode: true),
        ])

        let localVariableNillableHeaders: [String: Any?] = [
            :
        ]

        let localVariableHeaderParameters = APIHelper.rejectNilHeaders(localVariableNillableHeaders)

        let localVariableRequestBuilder: RequestBuilder<Void>.Type = APIAPI.requestBuilderFactory.getNonDecodableBuilder()

        return localVariableRequestBuilder.init(method: "GET", URLString: (localVariableUrlComponents?.string ?? localVariableURLString), parameters: localVariableParameters, headers: localVariableHeaderParameters, requiresAuthentication: false)
    }

    /**
     
     
     - parameter activityDto: (body)  
     - returns: Void
     */
    @available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 6.0, *)
    open class func updateActivity(activityDto: ActivityDto) async throws {
        return try await updateActivityWithRequestBuilder(activityDto: activityDto).execute().body
    }

    /**
     
     - PATCH /activity
     - parameter activityDto: (body)  
     - returns: RequestBuilder<Void> 
     */
    open class func updateActivityWithRequestBuilder(activityDto: ActivityDto) -> RequestBuilder<Void> {
        let localVariablePath = "/activity"
        let localVariableURLString = APIAPI.basePath + localVariablePath
        let localVariableParameters = JSONEncodingHelper.encodingParameters(forEncodableObject: activityDto)

        let localVariableUrlComponents = URLComponents(string: localVariableURLString)

        let localVariableNillableHeaders: [String: Any?] = [
            :
        ]

        let localVariableHeaderParameters = APIHelper.rejectNilHeaders(localVariableNillableHeaders)

        let localVariableRequestBuilder: RequestBuilder<Void>.Type = APIAPI.requestBuilderFactory.getNonDecodableBuilder()

        return localVariableRequestBuilder.init(method: "PATCH", URLString: (localVariableUrlComponents?.string ?? localVariableURLString), parameters: localVariableParameters, headers: localVariableHeaderParameters, requiresAuthentication: false)
    }
}
