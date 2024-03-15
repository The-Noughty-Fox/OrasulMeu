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
            dependencies: []
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
