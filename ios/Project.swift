import ProjectDescription

let project = Project(
    name: "OrasulMeu",
    targets: [
        .target(
            name: "OrasulMeu",
            destinations: .iOS,
            product: .app,
            bundleId: "io.tuist.OrasulMeu",
            infoPlist: .extendingDefault(
                with: [
                    "UILaunchStoryboardName": "LaunchScreen.storyboard",
                ]
            ),
            sources: ["OrasulMeu/Sources/**"],
            resources: ["OrasulMeu/Resources/**"],
            dependencies: [
                .external(name: "Nuke"),
                .external(name: "NukeUI"),
                .external(name: "MapboxMaps"),
                .external(name: "GoogleSignIn"),
                .external(name: "FacebookLogin"),
                .external(name: "NoughtyUI"),
                .external(name: "NoughtyEnvironment"),
                .external(name: "API")
            ]
        ),
        .target(
            name: "OrasulMeuTests",
            destinations: .iOS,
            product: .unitTests,
            bundleId: "io.tuist.OrasulMeuTests",
            infoPlist: .default,
            sources: ["OrasulMeu/Tests/**"],
            resources: [],
            dependencies: [.target(name: "OrasulMeu")]
        ),
    ]
)
