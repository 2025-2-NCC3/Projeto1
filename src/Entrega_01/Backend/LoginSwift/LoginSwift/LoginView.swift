import SwiftUI
import Alamofire

struct LoginView: View {
    @State private var username = ""
    @State private var password = ""
    @State private var navigateHome = false
    var body: some View {
        NavigationView {
            VStack {
                Text("Login")
                    .font(.largeTitle)
                    .padding(.bottom, 30)
                TextField("Username", text:$username)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal)
                SecureField("Password", text:$password)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal)
                Button("Login") {
                    actionLogin()
                }
                NavigationLink("Create Account", destination: CreateView())
                    .padding()
                
                NavigationLink(destination: HomeView(), isActive: $navigateHome) {
                    EmptyView()
                }.padding()
                    
            }
            .padding()
        }
    }
    
    func actionLogin() {
        let params = ["username": username, "password": password]
        AF.request("http://localhost:3000/login", method: .post, parameters: params, encoding: JSONEncoding.default).responseString { response in
                switch response.result {
                case .success(let value):
                    if(value == "Login successful") {
                        navigateHome = true
                    } else {
                        print(response)
                    }
                case .failure(let error):
                    print(error)
                }

        }
    }

}

#Preview {
    LoginView()
}

