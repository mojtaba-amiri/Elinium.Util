# Android Utility 

This is one small project to put some useful classes and tools and share it with others. Right now it includes:
- ## Get rid of register/unregister receivers. Use @OnBroadcastReceived 
This is useful when you want to register broadcast receiver for different actions. For instance if you want to do something when bluetooth state changes, you just need to set annotation for the function:
  
```sh
@OnBroadcastReceived(actionName=BluetoothAdapter.ACTION_STATE_CHANGED)
public void doSomething(){
}
```
 
  You have the option to specify the scope (is it local broadcast or public).
  
  You can use this annotation in every class that implements LifecycleOwner interface (Activity, Fragment, etc.). to initialize this, you have to do 
 
 ```sh
 BroadcastListener.initialize(this) 
 ```
 on onCreate event. 
  
- ## RecyclerView but easier: Add items and not more!
RecyclerAdapter and BaseItem which makes inserting different types of items into RecyclerView. For every item you should extend the BaseItem class like this:

```sh
public class SampleItem extends BaseItem<BaseViewHolder> 
```
 good point is you don't need to define ViewHolders. demo app has an example on how to use it. 
 
- ## Handling uncaught exceptions
ExceptionHandler is useful to handle all exceptions that are not caught by try/catch. It could be used in classes that implement LifecycleOwner.
