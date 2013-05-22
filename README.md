<a href="http://projectdanube.org/" target="_blank"><img src="http://peacekeeper.github.com/xdi2/images/projectdanube_logo.png" align="right"></a>
<img src="http://peacekeeper.github.com/xdi2/images/logo64.png"><br>

This is the integration project to deploy the [XDI2](http://github.com/projectdanube/xdi2) server as an "engine" in [Zephyr](http://github.com/airships/zephyr).

This is work in progress. 

### Information

* [Data Mapping](https://github.com/projectdanube/xdi2-zephyr/wiki/Data%20Mapping)
* Discovery {TODO}
* Multi Tenancy {TODO}
* [Code Example](https://github.com/projectdanube/xdi2-zephyr/wiki/Code%20Example)
* [Server Configuration Example](https://github.com/projectdanube/xdi2-zephyr/wiki/Server%20Configuration%20Example)

### How to build

First, you need to build the main [XDI2](http://github.com/projectdanube/xdi2) project.

Second, follow the instructions at [Zephyr](http://github.com/airships/zephyr).

You might have to repeat these initial steps frequently, as both XDI2 and Zephyr evolve.

After that, just run

    mvn clean install

To build all components.

### How to run

    mvn jetty:run

Then access the web interface at

	http://localhost:9990/

Or access the server's status page at

	http://localhost:9990/xdi

Or use an XDI client to send XDI messages to

    http://localhost:9990/xdi/zephyr

### How to build as XDI2 plugin

Run

    mvn clean install package -P xdi2-plugin

### Community

Google Group: http://groups.google.com/group/xdi2
