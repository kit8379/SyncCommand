🚀 SyncCommand 🚀

SyncCommand - the ultimate command bridge between multiple servers! Ever wished you could sync a command across multiple servers in a heartbeat? Now you can! Powered by the blazing fast Redis, SyncCommand delivers that promise.

🌟 About 🌟

SyncCommand is a robust and innovative plugin that enables the synchronization of commands across servers via Redis. Designed with performance and flexibility in mind, it offers server admins a seamless solution to manage cross-server commands, making multi-server setups more cohesive and interconnected.

🌟 Features 🌟

    🎮 Multi-Platform Support: Whether you're on Bukkit, Spigot, Paper, Folia, Velocity, or Bungee, we've got you covered!

    🔥 Redis-Powered Beyond Bungee's Limits: Leveraging the might of Redis, we've broken the chains! Bypass the constraints of Bungee plugin messaging which struggles with multiple proxies. Dive into limitless command synchronization in real-time!

    🌐 Multiple Channels Listening: Classify, categorize, and send commands uniquely to groups of servers. Multiple channels mean multiple avenues to orchestrate your server commands!

    🔒 Permissions Support: Safety first! With refined permissions, only the worthy shall pass. Ward off mischief-makers!

    🛠️ Personalized Configuration: Your server, your channel group! A configuration crafted to resonate with your needs.

    📜 Logs & Alerts: Stay ahead, stay informed! Monitor activities with precision, ensuring you never miss a beat!

🔧 Installation 🔧

    📥 Download the majestic SyncCommand plugin jar.
    📁 Pop it into your server's plugins folder.
    🔄 Restart that server. Watch SyncCommand rise!

🛠️ Configuration - It's Easy-Peasy! 🛠️

    📜 Open the config.yml file in your favorite text editor.
    📝 Edit the configuration to your liking.
    🔄 Restart your server. Watch SyncCommand rise!

config.yml (default):
```yaml
redis:
  host: "your_redis_server_here"
  port: 6379 # Default port, change if yours is different
  password: "Your_Secret_Password" # Keep it secret, keep it safe!
channels:
  - "GeneralChat"
  - "ModChat"
messages:
  reload: "&aFeel the magic! SyncCommand reloaded!"
  usage: "&cHey! Use like this: /sync <enable|disable>"
  noPermission: "&cNuh-uh! You can't do that here!"
  commandSynced: "&aBoom! Command synced to %s channel."
```
    
💬 Commands 💬

    /sync [channel] [command]: 🌉 Sync your commands to the specified channel
        permission → synccommand.admin
        Try this: /sync all tp @a 100 64 100

    /syncreload: 🔄 Spin the wheel, reload the plugin
        Permission → synccommand.admin

🔐 Permissions 🔐

    synccommand.admin: The golden key. Unlocks the world of SyncCommand.

📝 To-Do List 📝

    🧽 Sponge Integration: On our roadmap is the integration with Sponge, expanding our compatibility even further!

    📡 Bungee Messaging Support: We get it - setting up an extra Redis server might not be everyone's cup of tea. For those with a single proxy setup, we're in the works to add Bungee Messaging support. However, I strongly encourage delving into Redis for the most optimal and lag-free experience!

    🎮 Player Sudo Command Support: Enhancing command flexibility! Soon, you'll be able to make a player execute a command as if they typed it themselves. Stay tuned!

❓ Need Help? ❓

    Drop us a message anytime! We're always here, always listening. Your feedback fuels our fire! 🔥
    Let the magic of SyncCommand elevate your server game. Hit that download, and let’s get syncing! 🌟

📜 License 📜

SyncCommand is proudly offered under the MIT License. Here's what it means for you:

    🤝 Non-commercial Use: You're free to use SyncCommand on any of your servers. However, please don't sell it or package it as part of a commercial offering.

    🔄 Modification: Want to tweak something? Go ahead! You're free to modify SyncCommand for your personal/server use. Just don't distribute the modified version without our consent.

    🤲 Sharing: Love our plugin? Tell the world! However, if someone wants to use it, please direct them to our official SpigotMC page. No re-uploading or distributing through unofficial channels.

    📢 Attribution: If you're showcasing or discussing our plugin in videos, articles, or other media, a shoutout to the original SyncCommand SpigotMC page would be much appreciated!

    🚫 No Warranty: While we strive to offer the best, SyncCommand comes as-is, without any warranty. But worry not, our community is here to help with any hiccups you might encounter!

    🔗 Detailed License: For those who like the nitty-gritty, you can find the detailed license on GitHub repository.