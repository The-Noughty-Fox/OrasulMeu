//
//  UserStore.swift
//  OrasulMeu
//
//  Created by Lisnic Victor on 15.03.2024.
//

import Foundation
import NoughtyEnvironment
import NoughtyUI
import Combine
import UIKit

struct User: Codable, Equatable {
    let id: UUID
    let name: String
    let email: String
}

final class UserStore: ObservableObject {
    @Defaults("user") var user: User? = nil
    init() { }
}

final class AuthClient {
    static let live = AuthClient()
    private init() { }

    let auth = Authentication<User>.init(
        environment: .init(
            api: {
                .init { credential in
                    fatalError()
                } authenticateWithGoogle: { credendial in
                    fatalError()
                } authenticateWithFacebook: { credential in
                    fatalError()
                }
            },
            appleClient: { .live },
            googleClient: { .live }, 
            facebookClient: { .live },
            keychain: { .live }
        )
    )
}
