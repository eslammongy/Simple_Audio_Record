package com.eslammongy.simpleaudiorecord

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


class VoiceRecordAdapter(var context: Context, var listOfVoices: List<File>, onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<VoiceRecordAdapter.ViewHolder>() {

     private var removePositions:Int = 0
     private var removeItems:String = ""

    private var onItemClickListener:OnItemClickListener? = null
    init {
        this.onItemClickListener = onItemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater =
            LayoutInflater.from(context).inflate(R.layout.voice_recored_layout, parent, false)
        return ViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return listOfVoices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){

        holder.nameOfAudio.text = listOfVoices[position].name
        holder.timeOfAudio.text = CalculateTimeAgo().gitTimeAge(listOfVoices[position].lastModified())
    }

    inner class ViewHolder(view: View) :RecyclerView.ViewHolder(view), View.OnClickListener {

        var nameOfAudio = view.findViewById<TextView>(R.id.audioRecord_Name)!!
        var timeOfAudio = view.findViewById<TextView>(R.id.audioRecord_date)!!
        init{
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClickListener!!.onClick(listOfVoices[adapterPosition] , adapterPosition)
        }

    }

    interface OnItemClickListener {
        fun onClick(file: File, position: Int)
    }

    @Suppress("EqualsBetweenInconvertibleTypes")
     inner class  CalculateTimeAgo (){

        fun gitTimeAge(duration: Long):String{

            val date = Date()
            val seconds = TimeUnit.MILLISECONDS.toSeconds(date.time - duration)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(date.time - duration)
            val hours = TimeUnit.MILLISECONDS.toHours(date.time - duration)
            val days = TimeUnit.MILLISECONDS.toDays(date.time - duration)

            when {
                seconds < 60 -> {
                    return "Just Now"

                }
                minutes.equals(1) -> {
                    return "a minute ago"

                }
                minutes in 2..59 -> {
                    return "$minutes minute ago"

                }
                hours.equals(1) -> {
                    return "a hour ago"

                }
                hours in 2..23 -> {
                    return "$hours hours ago"

                }
                days.equals(1) -> {
                    return "a day ago"

                }
                else -> {
                    return "a days ago"
                }
            }
        }

    }
}


