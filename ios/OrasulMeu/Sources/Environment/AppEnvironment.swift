//
//  AppEnvironment.swift
//  OrasulMeu
//
//  Created by Lisnic Victor on 15.03.2024.
//

import Foundation
import NoughtyEnvironment

struct AppEnvironment {
    let userStore: UserStore
    let authentication: Authentication<User>

    static var current: Self {
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

        return .init(userStore: .init(), authentication: auth)
    }
}
