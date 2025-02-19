
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R

class OnboardingAdapter(// List of drawable resource IDs
    private val imageResIds: List<Int>
) :
    RecyclerView.Adapter<OnboardingAdapter.PageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.onboarding_page_item, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.imageView.setImageResource(imageResIds[position])
    }

    override fun getItemCount(): Int {
        return imageResIds.size
    }

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.page_image)
    }
}