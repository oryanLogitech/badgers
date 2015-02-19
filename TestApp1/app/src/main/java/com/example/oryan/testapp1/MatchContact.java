package com.example.oryan.testapp1;

/**
 * Created by oryan on 02/02/2015.
 */
public class MatchContact
{
    private String name;
    private String surname;
    private String id;
    private long lastDate;
    private int generation; //1 if it matches the first name suggestion, 2 for the next, etc.

    public MatchContact(String namein, String surnamein, long inDate, int inGen, String inId)
    {
        name = namein;
        surname = surnamein;
        lastDate = inDate;
        generation = inGen;
        id = inId;
    }

    public String getName()
    {
        return name;
    }

    public String getSurname() {return surname;}

    public long getLastDate()
    {
        return lastDate;
    }

    public int getGeneration()
    {
        return generation;
    }

    public String getId()
    {
        return id;
    }

}
