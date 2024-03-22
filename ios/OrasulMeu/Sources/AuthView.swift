//
//  AuthView.swift
//  OrasulMeu
//
//  Created by Lisnic Victor on 15.03.2024.
//

import SwiftUI

struct AuthView: View {
    var body: some View {
        VStack {
            Button("Login with apple") {
                Task {
                    do {
                        let user = try await AppEnvironment.current.authentication.authenticateWithApple()
                    } catch {
                        print(error)
                    }
                }
            }
            Button("Login with google") {}
            Button("Login with facebook") {}
        }
    }
}

#Preview {
    AuthView()
}
