import React, { useState } from 'react';
import {View, Text, Button, StyleSheet, TextInput, Alert} from 'react-native';
import { useNavigation }from '@react-navigation/native';

const CreateUserScreen = () => {
  const navigation = useNavigation();
  const [username, setUsarname] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');

  const handleCreateUser = async () => {
      try {
          const response =  await fetch('http://localhost:3000/user', {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                  username: username,
                  password: password,
                  email: email
              })
          });

          if (response.status === 201) {
              console.log('Create');
              Alert.alert(
                  'Success',
                  'User created successfully',
              );
              navigation.navigate('Login');
          } else {
              console.log('Something went wrong');
          }
      } catch (error) {
          console.log(error);
      }
  }

  return (
  <View style={styles.container}>
    <Text>Create User Screen</Text>
      <TextInput
      style={styles.input}
        placeholder="Email"
        onChangeText={text => setEmail(text)}
          value={email}
      />
      <TextInput
      style={styles.input}
        placeholder="User Name"
        onChangeText={text => setUsarname(text)}
          value={username }
      />
      <TextInput
          style={styles.input}          
          placeholder="Password"
        onChangeText={text => setPassword(text)}
          value={password}
        secureTextEntry={true}
      />
    <Button 
    title="Create Account"
    onPress= {handleCreateUser} />
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

export default CreateUserScreen;