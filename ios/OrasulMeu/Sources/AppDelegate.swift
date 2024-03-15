//
//  AppDelegate.swift
//  NoughtyEnvironment
//
//  Created by Lisnic Victor on 15.03.2024.
//

import Foundation
import UIKit
import GoogleSignIn

class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        

        return true
    }

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        let config = UISceneConfiguration(
            name: nil,
            sessionRole: connectingSceneSession.role
        )

        config.delegateClass = SceneDelegate.self

        return config
    }
}

class SceneDelegate: NSObject, UISceneDelegate {

}

