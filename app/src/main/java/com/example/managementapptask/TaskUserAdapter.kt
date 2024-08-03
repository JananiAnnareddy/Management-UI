package com.example.managementapptask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskUserAdapter(
    private var tasks: MutableList<Task>,
    private val onTaskStart: (Task) -> Unit
) : RecyclerView.Adapter<TaskUserAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.tn2)
        private val taskCreatedDate: TextView = itemView.findViewById(R.id.tcd2)
        private val startButton: Button = itemView.findViewById(R.id.startButton)

        fun bind(task: Task) {
            taskTitle.text = task.taskTitle
            taskCreatedDate.text = task.createdDate
            startButton.setOnClickListener { onTaskStart(task) }
        }
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
