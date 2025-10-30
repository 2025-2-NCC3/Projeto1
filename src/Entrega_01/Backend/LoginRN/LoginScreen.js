import React, { useState } from 'react';
import {View, Text, Button, StyleSheet, TextInput} from 'react-native';
import { useNavigation }from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const LoginScreen = () => {
    const navigation = useNavigation();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async () => {
        // http://localhost:3000/login  
        // POST /login
        //{
        //     "username" : "Andre",
        //     "password" : "123"
        // }
        try {
            const response =  await fetch('http://localhost:3000/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            if (response.status === 200) {
                // const data = await response.json();
                // console.log(data);
                console.log('Passou');
                await AsyncStorage.setItem('username', username);
                navigation.navigate('Home');
            } else {
                console.log('Something went wrong');
            }
        } catch (error) {
            console.log(error);
        }
    }

    return (
    <View style={styles.container}>
      <Text>Login</Text>
        <TextInput
        style={styles.input}
          placeholder="User Name"
          onChangeText={text => setUsername(text)}
            value={username}
        />
        <TextInput
            style={styles.input}          
            placeholder="Password"
          onChangeText={text => setPassword(text)}
            value={password}
          secureTextEntry={true}
        />
      <Button 
      title="Login"
      onPress= {handleLogin} />
      <Button 
      title="Create Account"
      onPress= {() => navigation.navigate('CreateUser') } />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20
  },
  input: {
    height: 40,
    width: '90%',
    borderColor: 'gray',
    borderWidth: 1,
    margin: 10
  }
});

export default LoginScreen;