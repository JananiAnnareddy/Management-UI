import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.managementapptask.R

class PrioritySpinnerAdapter(private val context: Context, private val priorities: List<Priority>) :
    BaseAdapter() {

    data class Priority(val icon: Int, val text: String)

    override fun getCount(): Int {
        return priorities.size
    }

    override fun getItem(position: Int): Any {
        return priorities[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item, parent, false)
        val priority = priorities[position]

        val icon: ImageView = view.findViewById(R.id.priorityIcon)
        val text: TextView = view.findViewById(R.id.priorityText)

        icon.setImageResource(priority.icon)
        text.text = priority.text

        return view
    }
}
