package com.android.parth

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class ApplicationClass : Application() {
    lateinit var activity: AppCompatActivity
    override fun onCreate() {
        super.onCreate()
        mInstance = this

    }

    companion object {
        lateinit var mInstance: ApplicationClass


        fun getmInstance(): ApplicationClass? {
            return mInstance
        }

        fun setmInstance(mInstance: ApplicationClass) {
            Companion.mInstance = mInstance
        }

        private operator fun get(context: Context): ApplicationClass {
            return context.applicationContext as ApplicationClass
        }

        fun create(context: Context): ApplicationClass {
            return get(context)
        }


    }

    fun setActity(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun getactivity(): AppCompatActivity {
        return this.activity
    }

}