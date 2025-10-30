import SwiftUI

struct CreateView: View {
    @State private var username = ""
    @State private var password = ""
    @State private var email = ""
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Create Account")
                    .font(.largeTitle)
                    .padding(.bottom, 30)
                TextField("Username", text:$username)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal)
                TextField("Email", text:$email)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal)
                
                SecureField("Password", text:$password)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal)
                Button("Create") {
                    actionCreate()
                }
                    
            }
            .padding()
        }
    }
    
    func actionCreate() {
        print("Create")
    }

}

#Preview {
    CreateView()
}


