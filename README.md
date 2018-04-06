# Arrowhead Framework Demonstrator applied to District Heating

This repository contains the code and documentation for a functional Industrial IoT (Internet of Things) or Cyber Physical System (CPS) System of Systems (SoS). It relies on the open source [Arrowhead Framework](https://forge.soa4d.org/plugins/mediawiki/wiki/arrowhead-f/index.php/Arrowhead_Framework_Wiki).

The domain of application is [district heating](https://en.wikipedia.org/wiki/District_heating). The choice of this domain is that it is at the intersection of industry (and Industry 4.0), smart grids, and home automation. The point of the demonstrator is to show a complete and functional implementation of a secure, interoperable, scalable, real time cyber physical system of systems that is in operation. (The adjectives used in the previous sentence are keywords for the project.)

In this project, we focus on a [single family home district heating substation](http://redan.danfoss.dk/PCMPDF/VLJVO101_VXi_Solo_lores.pdf). The limitation goes further to consider only space heating and excludes domestic hot tap water. In the initial release, we consider the control of the temperature of the hot water to the radiator based on the outdoor temperature. The control is achieved via a valve that regulates the primary fluid (from the factory) through the heat exchanger. In other word, there are two temperature sensors and one valve. The idea is simple!

By transforming the substation into a cyber physical system of systems, we are apparently complicated life very much. What required simple electronics before will now require several servers. The question is: "Is it worth it?" The answer is absolutely! The benefits are many. While maintaining end user comfort, the solution brings improved district heating operation (natural resources savings and lower operation costs). It also improves diagnostics of the heating system with condition monitoring. However, it is emphasized here that the use of the Arrowhead Framework simplifies the system, which is the real goal of this demonstrator.

## The Arrowhead Framework 
The [open source Arrowhead Framework](https://www.crcpress.com/IoT-Automation-Arrowhead-Framework/Delsing/p/book/9781498756754) is a Service Oriented Architecture (SOA) based structure that provides a suite of building blocks or core systems for Cyber Physical System and application-specific services to connect correctly and securely at runtime. Its central concept is to support most types of devices and systems in interacting with each other, and to enable the exchange of information, regardless of used protocols and semantic solutions. The framework, as per a SOA paradigm, enables loosely coupled services to communicate in a late binding fashion.

The suite of core system is divided in the mandatory core systems and the support systems. The mandatory core systems are the Service Registry, Orchestration, and Authorization systems. The dynamics are quite straight forward at runtime, service providers register their services with the service registry. Service consumers  ask the Orchestration system for a specific services. The Orchestration system  provides a service address after consulting the Service Registry and Authorization systems to the requesting consumer. In the single family home use case, the district heating system or application consumes services from the two temperature sensors and that of the valve (to get or set its position). It can obtain information or services from the weather station. To become Arrowhead compliant, a “wrapper” or “shell” was added to the weather station (which is connected via USB to the gateway). The idea of wrapper is described in the [RAMI 4.0](https://www.plattform-i40.de/I40/Redaktion/EN/Downloads/Publikation/rami40-an-introduction.pdf?__blob=publicationFile&v=4) documents where the device themselves are called assets.
## Core Systems GIT
* Service Registry (Mandatory)
* [Authentication, Authorization, and Accounting](/AAA) (Mandatory)
* Orchestration (Mandatory)
* Translator
* Historian
* Device Registry
* System Registry

## Application Systems GIT
* District heating application
* Weather station
* Temperature sensors (wireless and constrained servers)
* Valve (wireless and constrained server)
* Digital Twin
* zWave
* FiWare