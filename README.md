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


Btw ya un intégration Git dans Eclipse pis ca semble garder en mémoire le user/password :) Cherchez "EGit" dans le Eclipse Marketplace!
Une fois installé tu fais Right-Click > Team > tous les options de git :)