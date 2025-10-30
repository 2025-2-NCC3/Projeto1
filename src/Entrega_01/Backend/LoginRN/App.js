import LoginScreen from './LoginScreen';
import HomeScreen from './HomeScreen'
import CreateUserScreen from './CreateUserScreen'
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName='Login'>
        <Stack.Screen name='Login' component={LoginScreen} />
          <Stack.Screen name='Home' component={HomeScreen} />
          <Stack.Screen name='CreateUser' component={CreateUserScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

