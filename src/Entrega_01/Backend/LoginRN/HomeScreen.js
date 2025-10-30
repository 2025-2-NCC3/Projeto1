import React, { useState, useEffect } from 'react';
import {View, Text} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const HomeScreen = () => {
  const [username, setUsername] = useState('');

  const getUserName = async () => {
    // Get the username from the database
    try { 
      let userData = await AsyncStorage.getItem('username');
      setUsername(userData);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getUserName()
  }, []);

  return (
    <View>
      <Text>Welcome to  {username} Home</Text>
    </View>
  );
};
export default HomeScreen;