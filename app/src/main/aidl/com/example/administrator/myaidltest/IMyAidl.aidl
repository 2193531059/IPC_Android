package com.example.administrator.myaidltest;

import com.example.administrator.myaidltest.Person;
import com.example.administrator.myaidltest.IOnNewPersonArrviedListener;

interface IMyAidl{
    void addPerson(in Person person);
    List<Person> getPersonList();
    void registerNewPersonArrviedListener(IOnNewPersonArrviedListener listener);
    void unregisterNewPersonArrviedListener(IOnNewPersonArrviedListener listener);
}