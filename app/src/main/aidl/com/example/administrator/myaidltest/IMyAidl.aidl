package com.example.administrator.myaidltest;

import com.example.administrator.myaidltest.Person;

interface IMyAidl{
    void addPerson(in Person person);
    List<Person> getPersonList();
}