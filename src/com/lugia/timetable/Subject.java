/*
 * Copyright (c) 2013 Lugia Programming Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.lugia.timetable;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

final class Subject
{
    private String mSubjectCode;
    private String mSubjectDescription;
    private String mLectureSection;
    private String mTutorialSection;
    
    private int mCreditHours;
    private int mColor;
    
    private ArrayList<Schedule> mSchedule;
    
    public static final String JSON_SUBJECT_CODE        = "subjectCode";
    public static final String JSON_SUBJECT_DESCRIPTION = "subjectDescription";
    public static final String JSON_LECTURE_SECTION     = "lecturerSection";
    public static final String JSON_TUTORIAL_SECTION    = "tutorialSection";
    public static final String JSON_CREDIT_HOUR         = "creditHours";
    public static final String JSON_COLOR               = "color";
    public static final String JSON_SUBJECT_SCHEDULE    = "schedule";
    
    private static final String TAG = "Subject";
    
    public Subject(String code, String description, String lecturer, String tutorial, int credit)
    {
        this.mSubjectCode        = code;
        this.mSubjectDescription = description;
        this.mLectureSection     = lecturer;
        this.mTutorialSection    = tutorial;
        this.mCreditHours        = credit;
        
        this.mSchedule = new ArrayList<Schedule>();
    }
    
    public Subject(String code, String description, String lecturer, String tutorial, int credit, int color)
    {
        this.mSubjectCode        = code;
        this.mSubjectDescription = description;
        this.mLectureSection     = lecturer;
        this.mTutorialSection    = tutorial;
        this.mCreditHours        = credit;
        this.mColor              = color;
        
        this.mSchedule = new ArrayList<Schedule>();
    }
    
    public Subject(JSONObject json)
    {
        restoreFromJSON(json);
    }
    
    public String getSubjectCode()
    {
        return mSubjectCode;
    }
    
    public String getSubjectDescription()
    {
        return mSubjectDescription;
    }
    
    public String getLectureSection()
    {
        return mLectureSection;
    }
    
    public String getTutorialSection()
    {
        return mTutorialSection;
    }
    
    public int getCreditHours()
    {
        return mCreditHours;
    }
    
    public int getColor()
    {
        return mColor;
    }
    
    public ArrayList<Schedule> getSchedules()
    {
        return this.mSchedule;
    }
    
    public void setSubjectCode(String mSubjectCode)
    {
        this.mSubjectCode = mSubjectCode;
    }
    
    public void setSubjectDescription(String mSubjectDescription)
    {
        this.mSubjectDescription = mSubjectDescription;
    }
    
    public void setLectureSection(String mLectureSection)
    {
        this.mLectureSection = mLectureSection;
    }
    
    public void setTutorialSection(String mTutorialSection)
    {
        this.mTutorialSection = mTutorialSection;
    }
    
    public void setCreditHours(int creditHours)
    {
        this.mCreditHours = creditHours;
    }
    
    public void setColor(int color)
    {
        this.mColor = color;
    }
    
    public boolean hasLectureSection()
    {
        return mLectureSection != null;
    }
    
    public boolean hasTutorialSection()
    {
        return mTutorialSection != null;
    }
    
    public void addSchedule(int section, int day, int time, String room)
    {
        for (Schedule sTime : mSchedule)
        {
            if (sTime.getDay() != day)
                continue;
            
            // actually time is display in ascending order, this check maybe a litter over. 
            if (Math.abs(time - sTime.getTime()) == 1)
            {
                if (time < sTime.getTime())
                    sTime.setTime(time);
                
                sTime.setLength(sTime.getLength() + 1);
                
                return;
            }
        }
        
        Schedule sTime = new Schedule(section, day, time, 1, room);
        mSchedule.add(sTime);
    }
    
    public JSONObject getJSONObject()
    {
        JSONObject json = new JSONObject();
        
        try
        {
            json.put(JSON_SUBJECT_CODE,        mSubjectCode);
            json.put(JSON_SUBJECT_DESCRIPTION, mSubjectDescription);
            json.put(JSON_CREDIT_HOUR,         mCreditHours);
            json.put(JSON_COLOR,               mColor);
            
            json.put(JSON_LECTURE_SECTION,  hasLectureSection()  ? mLectureSection  : JSONObject.NULL);
            json.put(JSON_TUTORIAL_SECTION, hasTutorialSection() ? mTutorialSection : JSONObject.NULL);
            
            JSONArray timeArray = new JSONArray();
            
            for (Schedule time : mSchedule)
                timeArray.put(time.getJSONObject());
            
            json.put(JSON_SUBJECT_SCHEDULE, timeArray);
        }
        catch (JSONException e)
        {
            // something went wrong
            return null;
        }
        
        return json;
    }
    
    public void restoreFromJSON(JSONObject json)
    {
        try
        {
            this.mSubjectCode        = json.getString(JSON_SUBJECT_CODE);
            this.mSubjectDescription = json.getString(JSON_SUBJECT_DESCRIPTION);
            this.mCreditHours        = json.getInt(JSON_CREDIT_HOUR);
            this.mColor              = json.getInt(JSON_COLOR);
            
            this.mLectureSection  = !json.isNull(JSON_LECTURE_SECTION)  ? json.getString(JSON_LECTURE_SECTION)  : null;
            this.mTutorialSection = !json.isNull(JSON_TUTORIAL_SECTION) ? json.getString(JSON_TUTORIAL_SECTION) : null;
            
            this.mSchedule = new ArrayList<Schedule>();
            
            JSONObject timeObject;
            
            JSONArray timeArray = json.getJSONArray(JSON_SUBJECT_SCHEDULE);
            
            for (int i = 0; i < timeArray.length(); i++)
            {
                timeObject = timeArray.getJSONObject(i);
                
                this.mSchedule.add(new Schedule(timeObject));
            }
        }
        catch (Exception e)
        {
            // Something went wrong, so we need revert all change we made just now
            Log.e(TAG, "Error on restore", e);
            
            this.mSubjectCode        = "";
            this.mSubjectDescription = "";
            this.mLectureSection     = "";
            this.mTutorialSection    = "";
            this.mCreditHours        = 0;
            
            this.mSchedule = new ArrayList<Schedule>();
        }
    }
}
