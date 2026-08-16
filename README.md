# SyncCommand

A Minecraft plugin that synchronizes commands across multiple servers using Redis. It works as a Bukkit/Spigot/Paper/Folia, BungeeCord, and Velocity plugin.

## Why Redis

Unlike Bungee plugin messaging, which gets complicated with multiple proxies, Redis Pub/Sub lets any server publish a command to a channel that every other server listens on. This works regardless of how your network is structured.

## Features

- **Multi-platform** — Runs on Bukkit, Spigot, Paper, Folia, BungeeCord, and Velocity.
- **Multiple channels** — Send commands to specific groups of servers instead of the whole network.
- **Permission based** — Only players with the `synccommand.admin` permission can use the commands.
- **Customizable messages** — All messages are configurable in `config.yml`.

## Installation

1. Download the SyncCommand jar.
2. Put it in the `plugins` folder of each server (Bukkit and/or proxy).
3. Restart the servers.
4. Set the Redis host/port/password in `config.yml` and restart again.

## Configuration

```yaml
redis:
  host: "localhost"
  port: 6379
  password: ""

channels:
  - "channel1"
  - "channel2"

messages:
  reload: "&aSyncCommand configuration reloaded."
  usage: "&cUsage: /sync (Bukkit) or /syncb (Bungee) or /syncv (Velocity) <channel> <command>"
  noPermission: "&cYou do not have permission."
  commandSynced: "&aCommand synced to channel &e%s&a."
```

The `channels` list is just documentation of which channels exist. Any string can be used as a channel when running a sync command.

## Commands

| Platform | Command | Description |
| --- | --- | --- |
| Bukkit | `/sync <channel> <command>` | Publish a command to a channel |
| Bukkit | `/syncreload` | Reload the config |
| Bungee | `/syncb <channel> <command>` | Publish a command to a channel |
| Bungee | `/syncbreload` | Reload the config |
| Velocity | `/syncv <channel> <command>` | Publish a command to a channel |
| Velocity | `/syncvreload` | Reload the config |

All commands require the `synccommand.admin` permission, which defaults to `op`.

## License

[MIT](LICENSE)
