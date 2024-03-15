// swift-tools-version: 5.9
import PackageDescription

let package = Package(name: "PackageName",
                      dependencies: [
                        .package(url: "https://github.com/kean/Nuke.git", from: .init(12, 1, 6))
                      ]
)
