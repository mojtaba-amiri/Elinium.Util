This is one small project to put some useful classes and tools and share it with others. Right now it includes:
- Broadcast annotation which is useful when you want to register receiver for different actions. For instance if you want to do something when bluetooth state changes, you just need to set annotation for the function:
  
  @OnBroadcastReceived(actionName=BluetoothAdapter.ACTION_STATE_CHANGED)
  public void doSomething(){
  
  }
  
  You can use this annotation in every class that implements LifecycleOwner interface (Activity, Fragment, etc.). to initialize this, you have to do BroadcastListener.initialize(this) on create event. 
  
- RecyclerAdapter and BaseItem which makes inserting different types of items into RecyclerView. For every item you should extend the BaseItem class like this:
  public class SampleItem extends BaseItem<BaseViewHolder> 
 good point is you don't need to define ViewHolders. demo app has an example on how to use it and how easy it is. 
 
-  ExceptionHandler is useful to handle all exceptions that are not caught by try/catch. It could be used in every class that implements LifecycleOwner.
