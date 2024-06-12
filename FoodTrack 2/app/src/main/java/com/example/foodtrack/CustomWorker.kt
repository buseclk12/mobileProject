import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.util.Log

class CustomWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        Log.d("MyWorker", "Hello from MyWorker!")

        return Result.success()
    }
}
