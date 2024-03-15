import SwiftUI
import API

public struct ContentView: View {
    public init() {}

    @State var text = ""

    public var body: some View {
        Text(text)
            .padding()
            .task {
                APIAPI.basePath = "http://localhost:8080"
                do {
                    text = try await EchoAPI.getEcho()
                } catch {
                    print(error)
                }
            }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
