// swift-tools-version: 5.9
import PackageDescription

let package = Package(name: "PackageName",
                      dependencies: [
                        .package(url: "https://github.com/mapbox/mapbox-maps-ios.git", from: .init(11, 0, 0)),
                        .package(url: "https://github.com/kean/Nuke.git", from: .init(12, 1, 6)),
                        .package(url: "https://github.com/google/GoogleSignIn-iOS", from: .init(6, 0, 0)),
                        .package(url: "https://github.com/facebook/facebook-ios-sdk.git", from: .init(15, 0, 0)),
                        .package(url: "https://github.com/The-Noughty-Fox/noughty-environment-lib-swift.git", from: .init(0, 0, 1)),
                        .package(url: "https://github.com/The-Noughty-Fox/noughty-ui-lib-swift.git", from: .init(0, 0, 1))
                      ]
)
