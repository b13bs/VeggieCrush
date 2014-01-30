VeggieCrush
===========

Pour les highscores: SharedPreferences?
	http://developer.android.com/guide/topics/data/data-storage.html#pref

Pour les dialog (popup), exemple:
<pre>
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Confirmation")
		   .setMessage("bla bla")
		   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	       @Override
	       public void onClick(DialogInterface dialog, int id) {
	    	   //do stuff
	       }
	   })
	   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dialog, int id) {
	    	   //nope!!!
	       }
	   });				
	final Dialog d = builder.create();
	d.show();
</pre>
