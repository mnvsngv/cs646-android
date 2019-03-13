package com.mnvsngv.assignment3.views.dataclasses

data class Obstacle(var centreX: Float, var centreY: Float, var radius: Float,
                    var velocity: Float, var visible: Boolean, var hasHitPlayer: Boolean)