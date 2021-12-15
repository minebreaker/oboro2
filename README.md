# Oboro - Environment variable loader


# Example

```
$ python --version
Python 2.7

$ oboro load python3-profile
$ python --version
Python 3.5
```


# Profile

```json
{
  "variable": {
    "PATH": {
      "value": "C:\\lib\\jdk\\graalvm-ce-java17-windows-amd64-21.3.0\\graalvm-ce-java17-21.3.0\\bin",
      "conflict": "insert"
    },
    "JAVA_HOME": {
      "value": "C:\\lib\\jdk\\graalvm-ce-java17-windows-amd64-21.3.0\\graalvm-ce-java17-21.3.0",
      "conflict": "overwrite"
    }
  }
}
```

See [JSON Schema](https://github.com/minebreaker/oboro2/blob/master/profile.yaml) for the detail.

## Resolving

1. `./[[profile_name]].json`
2. `~/.oboro/[[profile_name]].json`


# TODOs

* .env
* yaml
* dry-run
* extends
